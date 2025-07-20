package com.si516.saludconecta.service.impl;

import com.si516.saludconecta.document.ClinicHistory;
import com.si516.saludconecta.document.Prescription;
import com.si516.saludconecta.document.Treatment;
import com.si516.saludconecta.dto.ClinicHistoryDTO;
import com.si516.saludconecta.event.TranscriptionCompletedEvent;
import com.si516.saludconecta.mapper.ClinicHistoryMapper;
import com.si516.saludconecta.repository.ClinicHistoryRepository;
import com.si516.saludconecta.service.ClinicHistoryService;
import com.si516.saludconecta.service.FileStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClinicHistoryServiceImpl implements ClinicHistoryService {

    private final ClinicHistoryRepository clinicHistoryRepository;
    private final FileStorageService fileStorageService;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;


    @Value("${app.transcriber.remote.url}")
    private String transcriberRemoteBaseUrl;

    @EventListener
    public void handleTranscriptionCompleted(TranscriptionCompletedEvent event) {
        log.info("Transcripción completada para audio: {}. Creando historia clínica automáticamente...", event.getAudioId());
        try {
            createFromTranscription(event.getAudioId());
            log.info("Historia clínica creada exitosamente para audio: {}", event.getAudioId());
        } catch (Exception e) {
            log.error("Error al crear historia clínica para audio {}: {}", event.getAudioId(), e.getMessage(), e);
        }
    }

    @Override
    public ClinicHistoryDTO createFromTranscription(String audioId) {
        // Obtener metadatos del archivo
        var fileMetadata = fileStorageService.getMetadata(audioId)
                .orElseThrow(() -> new RuntimeException("No se encontró el archivo de audio: " + audioId));

        // Extraer IDs desde metadata
        Map<String, Object> metadata = fileMetadata.metadata();
        String doctorId = (String) metadata.get("doctorId");
        String patientId = (String) metadata.get("patientId");

        // Llamar al microservicio de transcripción
        String transcriptionUrl = transcriberRemoteBaseUrl + "/transcribe/result/" + audioId;
        Map<String, Object> transcriptionJson = restTemplate.getForObject(transcriptionUrl, Map.class);

        // Obtener el objeto 'extracted' que contiene los datos estructurados
        Map<String, Object> extractedData = (Map<String, Object>) transcriptionJson.get("extracted");

        if (extractedData == null) {
            throw new RuntimeException("No se encontraron datos extraídos en la transcripción: " + audioId);
        }

        var clinicHistory = new ClinicHistory();

        // Mapear campos básicos desde extractedData
        clinicHistory.setVisitReason((String) extractedData.get("visitReason"));
        clinicHistory.setDiagnosis((String) extractedData.get("diagnosis"));

        // Mapear symptoms con validación
        List<String> symptoms = (List<String>) extractedData.get("symptoms");
        clinicHistory.setSymptoms(symptoms != null ? symptoms : List.of());

        // Mapear treatment con validación
        List<Map<String, Object>> treatmentList = (List<Map<String, Object>>) extractedData.get("treatment");
        if (treatmentList != null && !treatmentList.isEmpty()) {
            List<Treatment> treatments = treatmentList.stream()
                    .map(treatmentMap -> objectMapper.convertValue(treatmentMap, Treatment.class))
                    .collect(Collectors.toList());
            clinicHistory.setTreatment(treatments);
        } else {
            clinicHistory.setTreatment(List.of());
        }

        // Mapear prescription con validación
        Map<String, Object> prescriptionMap = (Map<String, Object>) extractedData.get("prescription");
        if (prescriptionMap != null) {
            Prescription prescription = objectMapper.convertValue(prescriptionMap, Prescription.class);
            clinicHistory.setPrescription(prescription);
        } else {
            // Crear prescription por defecto usando el enum correcto
            clinicHistory.setPrescription(new Prescription(com.si516.saludconecta.enums.PickupType.LATER, null));
        }

        // Usar los IDs reales desde los metadatos del archivo
        clinicHistory.setDoctorId(doctorId);
        clinicHistory.setPatientId(patientId);

        clinicHistory.setCreatedAt(Instant.now());

        var saved = clinicHistoryRepository.save(clinicHistory);
        return ClinicHistoryMapper.toDTO(saved);
    }

    @Override
    public List<ClinicHistoryDTO> getAll() {
        return clinicHistoryRepository.findAll().stream()
                .map(ClinicHistoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ClinicHistoryDTO getById(String id) {
        var clinicHistory = clinicHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ClinicHistory not found: " + id));
        return ClinicHistoryMapper.toDTO(clinicHistory);
    }

    @Override
    public ClinicHistoryDTO create(ClinicHistoryDTO dto) {
        var clinicHistory = ClinicHistoryMapper.toEntity(dto);
        clinicHistory.setCreatedAt(Instant.now());
        var saved = clinicHistoryRepository.save(clinicHistory);
        return ClinicHistoryMapper.toDTO(saved);
    }

    @Override
    public ClinicHistoryDTO update(String id, ClinicHistoryDTO dto) {
        var existing = clinicHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ClinicHistory not found: " + id));

        var updated = ClinicHistoryMapper.toEntity(dto);
        updated.setId(existing.getId());
        updated.setCreatedAt(existing.getCreatedAt());

        var saved = clinicHistoryRepository.save(updated);
        return ClinicHistoryMapper.toDTO(saved);
    }

    @Override
    public void delete(String id) {
        clinicHistoryRepository.deleteById(id);
    }
}