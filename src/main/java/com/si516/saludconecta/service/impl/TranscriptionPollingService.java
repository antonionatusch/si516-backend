package com.si516.saludconecta.service.impl;

import com.si516.saludconecta.event.TranscriptionCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranscriptionPollingService {

    private final ApplicationEventPublisher eventPublisher;
    private final RestTemplate restTemplate;

    @Value("${app.transcriber.remote.url}")
    private String transcriberRemoteBaseUrl;

    @Async
    public void pollTranscriptionStatus(String audioId) {
        log.info("Iniciando polling para audio: {}", audioId);

        String statusUrl = transcriberRemoteBaseUrl + "/transcribe/status/" + audioId;

        try {
            // Polling cada 10 segundos por máximo 5 minutos
            int maxAttempts = 30;
            int attempt = 0;

            while (attempt < maxAttempts) {
                try {
                    Map<String, Object> statusResponse = restTemplate.getForObject(statusUrl, Map.class);

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

                    CompletableFuture.runAsync(() -> {
                    }, CompletableFuture.delayedExecutor(10, TimeUnit.SECONDS)).join();
                    attempt++;

                } catch (Exception e) {
                    log.warn("Error en intento {} para audio {}: {}", attempt + 1, audioId, e.getMessage());
                    CompletableFuture.runAsync(() -> {
                    }, CompletableFuture.delayedExecutor(10, TimeUnit.SECONDS)).join();
                    attempt++;
                }
            }

            if (attempt >= maxAttempts) {
                log.error("Timeout en polling para audio: {}. Se agotaron los intentos.", audioId);
            }

        } catch (Exception e) {
            log.error("Error crítico en polling para audio {}: {}", audioId, e.getMessage(), e);
        }

        CompletableFuture.completedFuture(null);
    }
}