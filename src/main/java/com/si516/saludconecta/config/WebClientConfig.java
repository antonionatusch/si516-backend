package com.si516.saludconecta.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient transcriberWebClient(
            @Value("${app.transcriber.remote.connect-timeout-ms:3000}") int connectTimeoutMs,
            @Value("${app.transcriber.remote.read-timeout-ms:600000}") int readTimeoutMs
    ) {

        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofMillis(readTimeoutMs))
                ;

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(cfg -> cfg.defaultCodecs().maxInMemorySize(8 * 1024 * 1024)) // 8MB
                .build();

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(strategies)
                .build();
    }
}
