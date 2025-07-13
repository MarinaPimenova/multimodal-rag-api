package com.training.ai.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.ai.openai")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIConfig {

    private String apiKey;
    private String baseUrl;
    private Chat chat;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Chat {
        private String completionsPath;
        private String model;
        private String embeddingsPath;
    }
}
