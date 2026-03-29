package com.training.ai.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.ai.cohere")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CohereProperties {
    private String apiKey;
    private String baseUrl;
    private Chat chat;
    private EmbedConfig embed;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EmbedConfig {
        private String model;
        private String inputType;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Chat {
        private String model;
    }
}
