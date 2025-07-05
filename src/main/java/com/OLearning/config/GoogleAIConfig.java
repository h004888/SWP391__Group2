package com.OLearning.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GoogleAIConfig {

    @Value("${google.ai.api.key}")
    private String apiKey;

    @Value("${google.ai.api.url}")
    private String apiUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiUrl() {
        return apiUrl;
    }
} 