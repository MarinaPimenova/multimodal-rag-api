package com.training.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.oci.cohere.OCICohereChatModel;
import org.springframework.ai.oci.cohere.OCICohereChatOptions;
import org.springframework.ai.oci.cohere.api.CohereApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CohereApiConfig {

    @Bean
    public ChatModel cohereChatModel(CohereApi cohereApi, CohereProperties cohereProperties) {
        OCICohereChatOptions options = OCICohereChatOptions.builder()
                .model(cohereProperties.getChat().getModel())
                .temperature(0.7)
                .build();

        return new OCICohereChatModel(cohereApi, options);
    }

    @Bean
    public ChatClient cohereChatClient(ChatModel cohereChatModel) {
        return ChatClient.builder(cohereChatModel).build();
    }
}