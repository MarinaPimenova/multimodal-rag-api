package com.training.ai.config;

import com.training.ai.config.model.ChatRequest;
import com.training.ai.config.model.CohereResponse;
import com.training.ai.config.model.EmbeddingRequest;
import com.training.ai.config.model.EmbeddingResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.oci.cohere.OCICohereChatModel;
import org.springframework.ai.oci.cohere.OCICohereChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClient;

import java.util.List;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Configuration
@RequiredArgsConstructor
public class CohereApiConfig {
    private final CohereProperties cohereProperties;

    @Bean
    public CohereApi cohereApi(RestClient.Builder restClientBuilder) {
        DefaultResponseErrorHandler responseErrorHandler = new DefaultResponseErrorHandler();

        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + cohereProperties.getApiKey());
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "application/json");

        RestClient restClient = restClientBuilder
                .baseUrl(cohereProperties.getBaseUrl())
                .defaultHeaders(h -> h.addAll(headers))
                .build();

        return new CohereApi(restClient, cohereProperties);
    }

    /**
     * Cohere API client for chat completion and embeddings
     */
    @RequiredArgsConstructor
    public static class CohereApi {
        private final RestClient restClient;
        private final CohereProperties cohereConfig;

        /**
         * Send a chat message to Cohere API
         * Corresponds to: POST /v2/chat
         */
        public CohereResponse chatCompletion(List<ChatRequest.Message> messages) {
            return chatCompletion(messages, false);
        }

        /**
         * Send a chat message to Cohere API with streaming option
         */
        public CohereResponse chatCompletion(List<ChatRequest.Message> messages, boolean stream) {
            ChatRequest request = new ChatRequest(
                    cohereConfig.getChat().getModel(),
                    messages,
                    stream
            );

            return restClient
                    .post()
                    .uri("/chat")
                    .body(request)
                    .retrieve()
                    .body(CohereResponse.class);
        }

        /**
         * Generate embeddings for the given texts
         * Corresponds to: POST /v2/embed
         */
        public EmbeddingResponse generateEmbeddings(List<String> texts) {
            return generateEmbeddings(texts, cohereConfig.getEmbed().getInputType());
        }

        /**
         * Generate embeddings with custom input type
         */
        public EmbeddingResponse generateEmbeddings(List<String> texts, String inputType) {
            EmbeddingRequest request = new EmbeddingRequest(
                    cohereConfig.getEmbed().getModel(),
                    texts,
                    inputType,
                    new String[]{"float"}
            );

            return restClient
                    .post()
                    .uri("/embed")
                    .body(request)
                    .retrieve()
                    .body(EmbeddingResponse.class);
        }
    }


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