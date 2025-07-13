package com.training.ai.rag.service;

import com.training.ai.rag.model.AIGenerativeResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.UUID;

@Service
public class RagService {
    private static final String NEW_SESSION_ID = "1";

    private String template = """
            You're assisting with questions.
            Use the following context and chat history to answer the QUESTION but act as if you knew this information innately.
            If unsure, simply state that you don't know.
                        
            QUESTION
            {question}
                        
            """;
    @Value("classpath:/system-prompt-template.st")
    private Resource systemPrompt;
    private final VectorStore vectorStore;
    private final ChatMemory chatMemory;
    private final ChatClient chatClient;

    public RagService(
            VectorStore vectorStore,
            ChatMemory chatMemory, ChatClient chatClient) {
        this.vectorStore = vectorStore;
        this.chatMemory = chatMemory;
        this.chatClient = chatClient;
    }

    public AIGenerativeResponse generate(String sessionId, String question) {
        Assert.notNull(sessionId, "sessionId cannot be null");
        Assert.notNull(question, "question cannot be null");
        PromptTemplate pt = new PromptTemplate(template);
        Prompt p = pt.create(Map.of("question", question));
        if(NEW_SESSION_ID.equals(sessionId)) {
            sessionId = UUID.randomUUID().toString();
        }
        String content = chatClient
                .prompt(p)
                .system(systemSpec -> systemSpec.text(systemPrompt)
                        .param("question", question))
                .advisors(
                        promptChatMemoryAdvisor(sessionId),
                        new QuestionAnswerAdvisor(vectorStore),
                        new SimpleLoggerAdvisor())
                .call()
                .content();
        return AIGenerativeResponse.builder()
                .sessionId(sessionId)
                .content(content)
                .build();
    }

    protected PromptChatMemoryAdvisor promptChatMemoryAdvisor(String conversationId) {
        return PromptChatMemoryAdvisor
                .builder(chatMemory)
                .conversationId(conversationId)
                .build();
    }
}

