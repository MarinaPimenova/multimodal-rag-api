package com.training.ai.embadding.web;

import com.training.ai.embadding.image.ImageInsightService;
import com.training.ai.embadding.store.VectorStoreService;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;

import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WebPageReaderService {

    private final VectorStore vectorStore;
    private final ImageInsightService imageInsightService;
    private final VectorStoreService vectorStoreService;
    private final HtmlTableExtractorService htmlTableExtractorService;
    private final ChatClient chatTableClient;

    public void addWebPageContent(String url) {
        try {
            addPlainWebPageContent(url);
            extractDiagrams(url);
            //addTablesDescriptionOfWebPageContent(url);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read from URL: " + url + "Caused by: " + e.getMessage(), e);
        }
    }

    protected void addTablesDescriptionOfWebPageContent(String url) throws IOException {

        String tableHtml = htmlTableExtractorService.extractTablesAsHtml(url);

        String prompt = """
                Please analyze and summarize the following HTML tables. 
                Convert data into meaningful descriptions that can be embedded for search.
                HTML:
                """ + tableHtml;
        String content = chatTableClient
                .prompt(prompt)
                .advisors(
                        new QuestionAnswerAdvisor(vectorStore),
                        new SimpleLoggerAdvisor())
                .call()
                .content();

        // Now embed the response into vector store
        // Convert into Document list
        vectorStoreService.storeToVectorStore(content, url);
    }

    protected void addPlainWebPageContent(String url) throws IOException {
        Document htmlDoc = Jsoup.connect(url).get();
        // Extract readable content
        String textContent = htmlDoc.select("article, main, body").text(); // adjust selector as needed
        if (textContent.isBlank()) {
            throw new RuntimeException("No readable content found at URL: " + url);
        }
        // Convert into Document list
        vectorStoreService.storeToVectorStore(textContent, url);
    }

    protected List<String> extractImageUrlsFromPage(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        return doc.select("img")
                .stream()
                .map(img -> img.absUrl("src"))
                .filter(src -> src.endsWith(".png") || src.endsWith(".jpg"))
                .toList();
    }

    public void extractDiagrams(String webpageUrl) throws IOException {
        List<String> imageUrls = extractImageUrlsFromPage(webpageUrl);
        for (String imageUrl : imageUrls) {
            imageInsightService.extractInsightAndStore(imageUrl);
        }

    }
}

