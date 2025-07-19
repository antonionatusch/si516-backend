package com.si516.saludconecta.listener;

import com.si516.saludconecta.event.NewAudioStoredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class RemoteTranscriptionNotifier {

    private final WebClient transcriberWebClient;

    @Value("${app.transcriber.remote.enabled:true}")
    private boolean enabled;

    @Value("${app.transcriber.remote.url}")
    private String remoteBaseUrl;

    @Value("${app.transcriber.remote.api-key:}")
    private String apiKey;

    @EventListener
    public void onNewAudio(NewAudioStoredEvent event) {
        if (!enabled) {
            log.debug("Transcriber remoto deshabilitado (fileId={})", event.getFileId());
            return;
        }

        String url = remoteBaseUrl + "/transcribe/start?fileId=" + event.getFileId();
        log.info("Notificando transcriber remoto: {}", url);

        WebClient.RequestHeadersSpec<?> req = transcriberWebClient.post().uri(url);
        if (!apiKey.isBlank()) {
            req = req.header("X-Api-Key", apiKey);
        }

        req.retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(15))
                .doOnError(ex -> log.error("Error notificando transcriber (fileId={}): {}", event.getFileId(), ex.getMessage()))
                .onErrorResume(ex -> Mono.empty())
                .subscribe(resp -> log.info("Respuesta transcriber fileId={}: {}", event.getFileId(), resp));
    }
}
