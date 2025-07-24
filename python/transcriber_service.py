"""
Servicio de transcripción y extracción estructurada para audios subidos al backend.

Flow:
  1. Backend sube audio a GridFS y notifica: POST /transcribe/start?fileId=<id>
  2. Este servicio descarga el audio: GET <BACKEND_BASE_URL>/files/<id>
  3. Convierte .webm -> .wav (16 kHz mono), divide por silencios, transcribe con Whisper.
  4. Envía texto al LLM (API estilo OpenAI) para extraer JSON estructurado.
  5. Exponer estado y resultado por endpoints.

Endpoints:
  POST /transcribe/start?fileId=...
  GET  /transcribe/status/{fileId}
  GET  /transcribe/result/{fileId}
  GET  /health
"""

import os
import json
import uuid
import subprocess
import threading
import traceback
from datetime import datetime
from typing import Dict, Optional

import requests
from fastapi import FastAPI, HTTPException, Query
from fastapi.responses import JSONResponse
from pydub import AudioSegment
from pydub.silence import split_on_silence
import whisper
from openai import OpenAI
from fastapi.middleware.cors import CORSMiddleware


# ===================== CONFIG (ENV VARS) =====================

BACKEND_BASE_URL = os.getenv("BACKEND_BASE_URL", "https://1bb7741507ee.ngrok-free.app/")
LLM_API_BASE     = os.getenv("LLM_API_BASE", "http://localhost:1234/v1")
LLM_API_KEY      = os.getenv("LLM_API_KEY", "not-needed")    # si tu servidor ignora key, déjalo
WHISPER_MODEL    = os.getenv("WHISPER_MODEL", "large-v3")
WORK_DIR         = os.getenv("WORK_DIR", "./work")

# Callback (opcional) para crear historia clínica automáticamente
CALLBACK_HISTORIES_ENABLED = os.getenv("CALLBACK_HISTORIES_ENABLED", "false").lower() == "true"
CALLBACK_HISTORIES_URL     = os.getenv("CALLBACK_HISTORIES_URL", f"{BACKEND_BASE_URL}/histories")

os.makedirs(WORK_DIR, exist_ok=True)

PROMPT_DEL_SISTEMA = """
Eres un asistente médico experto en análisis de texto. A partir de la siguiente transcripción de una consulta,
extrae SOLO los campos solicitados y devuelve JSON válido. Si algo no se menciona usa "No especificado".
No expliques, no agregues texto fuera del JSON.

Campos requeridos (respeta exactamente las llaves y tipos):

{
  "patientName": "string",
  "visitReason": "string",
  "diagnosis": "string",
  "symptoms": ["string", "..."],
  "treatment": [
    {
      "drug": "string",
      "lab": "string",
      "instruction": "string"
    }
  ],
  "pickup": {
    "pickupType": "NOW|SCHEDULED|LATER",
    "scheduledTime": "HH:MM o 'No especificado'"
  }
}

Ajusta 'pickupType' a SCHEDULED solo si el texto da hora específica; si no, NOW o LATER según contexto.
NO inventes campos, NO añadas comentarios.
"""

# ===================== ESTADOS EN MEMORIA =====================

class TranscriptionStatus:
    def __init__(self, file_id: str):
        self.file_id = file_id
        self.state = "PENDING"          # PENDING | RUNNING | DONE | ERROR
        self.error: Optional[str] = None
        self.started_at: Optional[str] = None
        self.finished_at: Optional[str] = None
        self.text: Optional[str] = None
        self.result_json: Optional[dict] = None
        self.internal_id = str(uuid.uuid4())

STATUSES: Dict[str, TranscriptionStatus] = {}
LOCK = threading.Lock()

# ===================== CARGA MODELO Y CLIENTE LLM =====================

print(f"[BOOT] Cargando modelo Whisper '{WHISPER_MODEL}' ...")
whisper_model = whisper.load_model(WHISPER_MODEL)
print("[BOOT] ✅ Whisper cargado.")

client_llm = OpenAI(base_url=LLM_API_BASE, api_key=LLM_API_KEY)

# ===================== UTILIDADES =====================

def download_audio(file_id: str) -> str:
    url = f"{BACKEND_BASE_URL}/files/{file_id}"
    local_path = os.path.join(WORK_DIR, f"{file_id}.webm")
    r = requests.get(url, timeout=180)
    if r.status_code != 200:
        raise RuntimeError(f"Descarga falló status={r.status_code} url={url}")
    with open(local_path, "wb") as f:
        f.write(r.content)
    return local_path

def convert_to_wav(path_webm: str) -> str:
    base, _ = os.path.splitext(path_webm)
    wav_path = base + ".wav"
    cmd = [
        "ffmpeg", "-y", "-i", path_webm,
        "-ar", "16000", "-ac", "1", "-c:a", "pcm_s16le",
        wav_path
    ]
    subprocess.run(cmd, check=True, capture_output=True)
    return wav_path

def transcribe_with_chunks(wav_path: str) -> str:
    audio = AudioSegment.from_wav(wav_path)
    chunks = split_on_silence(
        audio,
        min_silence_len=700,
        silence_thresh=-40,
        keep_silence=200
    )
    parts = []
    for i, chunk in enumerate(chunks):
        temp_chunk = os.path.join(WORK_DIR, f"chunk_{uuid.uuid4().hex}_{i}.wav")
        chunk.export(temp_chunk, format="wav")
        try:
            res = whisper_model.transcribe(temp_chunk, language="es")
            t = res.get("text", "").strip()
            if t:
                parts.append(t)
        finally:
            if os.path.exists(temp_chunk):
                os.remove(temp_chunk)
    return "\n".join(parts)

def call_llm(texto: str) -> dict:
    completion = client_llm.chat.completions.create(
        model="local-model",     # Ajusta si en LM Studio el modelo tiene otro nombre
        messages=[
            {"role": "system", "content": PROMPT_DEL_SISTEMA},
            {"role": "user", "content": texto}
        ],
        temperature=0.15,
    )
    raw = completion.choices[0].message.content
    try:
        return json.loads(raw)
    except json.JSONDecodeError:
        cleaned = raw.strip().strip("`")
        try:
            return json.loads(cleaned)
        except Exception:
            return {"error": "JSON parse error", "raw": raw[:700]}

def normalize(data: dict) -> dict:
    plantilla = {
        "patientName": "No especificado",
        "visitReason": "No especificado",
        "diagnosis": "No especificado",
        "symptoms": [],
        "treatment": [],
        "pickup": {
            "pickupType": "NOW",
            "scheduledTime": "No especificado"
        }
    }
    def merge(base, incoming):
        if not isinstance(incoming, dict):
            return base
        for k, v in base.items():
            if k in incoming:
                if isinstance(v, dict):
                    base[k] = merge(v, incoming[k])
                else:
                    base[k] = incoming[k]
        return base
    return merge(plantilla, data if isinstance(data, dict) else {})

# ===================== WORKER =====================

def process_file(file_id: str):
    with LOCK:
        st = STATUSES.get(file_id)
        if not st:
            st = TranscriptionStatus(file_id)
            STATUSES[file_id] = st
        st.state = "RUNNING"
        st.started_at = datetime.utcnow().isoformat() + "Z"

    try:
        webm = download_audio(file_id)
        wav = convert_to_wav(webm)
        text_full = transcribe_with_chunks(wav)
        with LOCK:
            st.text = text_full

        extracted_raw = call_llm(text_full)
        extracted_norm = normalize(extracted_raw)

        final_payload = {
            "fileId": file_id,
            "transcribedAt": datetime.utcnow().isoformat() + "Z",
            "text": text_full,
            "extracted": extracted_norm
        }

        with LOCK:
            st.result_json = final_payload
            st.state = "DONE"
            st.finished_at = datetime.utcnow().isoformat() + "Z"

        if CALLBACK_HISTORIES_ENABLED:
            try:
                r = requests.post(CALLBACK_HISTORIES_URL, json=final_payload, timeout=30)
                print(f"[CALLBACK] POST {CALLBACK_HISTORIES_URL} status={r.status_code}")
            except Exception as cb_e:
                print(f"[CALLBACK] Error callback: {cb_e}")

    except Exception as e:
        tb = traceback.format_exc()
        with LOCK:
            st.state = "ERROR"
            st.error = str(e)
            st.finished_at = datetime.utcnow().isoformat() + "Z"
        print(f"[ERROR] fileId={file_id}: {e}\n{tb}")
    finally:
        # Limpieza asociada a ese file_id (webm / wav)
        try:
            for fname in os.listdir(WORK_DIR):
                if fname.startswith(file_id) and (fname.endswith(".webm") or fname.endswith(".wav")):
                    os.remove(os.path.join(WORK_DIR, fname))
        except Exception:
            pass

# ===================== FASTAPI APP =====================

app = FastAPI(title="Transcriber Service", version="1.1.0")
origins = ["*"]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.post("/transcribe/start")
def start(fileId: str = Query(..., alias="fileId")):
    with LOCK:
        existing = STATUSES.get(fileId)
        if existing and existing.state in ("RUNNING", "PENDING"):
            return {"message": "Already in progress", "fileId": fileId}
        if existing and existing.state == "DONE":
            return {"message": "Already done", "fileId": fileId}
        st = TranscriptionStatus(fileId)
        STATUSES[fileId] = st
    t = threading.Thread(target=process_file, args=(fileId,), daemon=True)
    t.start()
    return {"message": "Accepted", "fileId": fileId}

@app.get("/transcribe/status/{file_id}")
def status(file_id: str):
    st = STATUSES.get(file_id)
    if not st:
        raise HTTPException(404, "Not found")
    return {
        "fileId": st.file_id,
        "state": st.state,
        "error": st.error,
        "startedAt": st.started_at,
        "finishedAt": st.finished_at
    }

@app.get("/transcribe/result/{file_id}")
def result(file_id: str):
    st = STATUSES.get(file_id)
    if not st:
        raise HTTPException(404, "Not found")
    if st.state != "DONE":
        return JSONResponse(
            status_code=409,
            content={"message": "Not finished", "state": st.state, "fileId": file_id}
        )
    return st.result_json

@app.get("/health")
def health():
    return {"status": "ok", "model": WHISPER_MODEL}

# ===================== MAIN =====================

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("transcriber_service:app", host="0.0.0.0", port=8001, reload=False)