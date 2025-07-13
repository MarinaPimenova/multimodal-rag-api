package com.training.ai.embadding.store;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VectorStoreService {

    private final VectorStore vectorStore;

    public void storeToVectorStore(String info, String imageUrl) {
        // Convert to document and embed
        Document doc = new Document(info);
        doc.getMetadata()
                .put("source", imageUrl);

        TokenTextSplitter splitter = new TokenTextSplitter();
        vectorStore.accept(splitter.apply(List.of(doc)));
    }
}
