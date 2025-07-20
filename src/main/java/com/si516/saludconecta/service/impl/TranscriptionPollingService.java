package com.si516.saludconecta.service.impl;

import com.si516.saludconecta.event.TranscriptionCompletedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranscriptionPollingService {

    private final ApplicationEventPublisher eventPublisher;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Async
    public CompletableFuture<Void> pollTranscriptionStatus(String audioId) {
        log.info("Iniciando polling para audio: {}", audioId);

        String statusUrl = "http://25.51.135.130:8001/transcribe/status/" + audioId;

        try {
            // Polling cada 10 segundos por máximo 5 minutos
            int maxAttempts = 30;
            int attempt = 0;

            while (attempt < maxAttempts) {
                try {
                    Map<String, Object> statusResponse = restTemplate.getForObject(statusUrl, Map.class);

                    if (statusResponse != null) {
                        String state = (String) statusResponse.get("state");

                        log.info("Estado de transcripción para {}: {}", audioId, state);

                        if ("DONE".equals(state)) {
                            log.info("Transcripción completada para {}. Publicando evento...", audioId);
                            eventPublisher.publishEvent(new TranscriptionCompletedEvent(this, audioId));
                            break;
                        } else if ("ERROR".equals(state)) {
                            String error = (String) statusResponse.get("error");
                            log.error("Error en transcripción para {}: {}", audioId, error);
                            break;
                        }
                        // Si está en RUNNING, continúa el polling
                    }

                    Thread.sleep(10000); // Esperar 10 segundos
                    attempt++;

                } catch (Exception e) {
                    log.warn("Error en intento {} para audio {}: {}", attempt + 1, audioId, e.getMessage());
                    Thread.sleep(10000);
                    attempt++;
                }
            }

            if (attempt >= maxAttempts) {
                log.error("Timeout en polling para audio: {}. Se agotaron los intentos.", audioId);
            }

        } catch (Exception e) {
            log.error("Error crítico en polling para audio {}: {}", audioId, e.getMessage(), e);
        }

        return CompletableFuture.completedFuture(null);
    }
}