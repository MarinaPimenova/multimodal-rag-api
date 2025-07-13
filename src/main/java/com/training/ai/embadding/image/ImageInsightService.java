package com.training.ai.embadding.image;

import com.training.ai.embadding.store.VectorStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;

import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class ImageInsightService {

    private final VectorStore vectorStore;
    private final ChatClient chatImageClient;
    private final VectorStoreService vectorStoreService;

    public void extractInsightAndStore(String imageUrl) throws IOException {

        String content = chatImageClient
                .prompt("Analyze this diagram and describe key insights.")
                .advisors(
                        new QuestionAnswerAdvisor(vectorStore),
                        new SimpleLoggerAdvisor())
                .call()
                .content();

        vectorStoreService.storeToVectorStore(content, imageUrl);
    }

    public String downloadImageAsBase64(String imageUrl) throws IOException {
        try (InputStream in = new URL(imageUrl).openStream()) {
            byte[] imageBytes = in.readAllBytes();
            return Base64.getEncoder().encodeToString(imageBytes);
        }
    }
}

