package com.training.ai.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.model.ApiKey;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;


@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Configuration
@RequiredArgsConstructor
public class OpenAiApiConfig {
    private final AIConfig aiConfig;

    @Bean
    public OpenAiApi openAiApi(
            RestClient.Builder restClientBuilder,
            WebClient.Builder webClientBuilder) {
        DefaultResponseErrorHandler responseErrorHandler = new DefaultResponseErrorHandler();

        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("cache-control", "no-cache");
        headers.add("Api-Key", aiConfig.getApiKey());
        ApiKey apiKey = new SimpleApiKey(aiConfig.getApiKey());

        return new OpenAiApi(aiConfig.getBaseUrl(), apiKey, headers,
                aiConfig.getChat().getCompletionsPath(),//"/openai/deployments/gpt-4.1-mini-2025-04-14/chat/completions",
                aiConfig.getChat().getEmbeddingsPath(), //"/openai/deployments/text-embedding-ada-002/embeddings",
                restClientBuilder, webClientBuilder, responseErrorHandler
        );
    }

    @Bean
    public ChatModel chatModel(OpenAiApi openAiApi) {
        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
                .model(aiConfig.getChat().getModel())
                .streamUsage(false)
                .temperature(0.0)
                .build();
        return OpenAiChatModel.builder()
                .defaultOptions(openAiChatOptions)
                .openAiApi(openAiApi)
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }
}
