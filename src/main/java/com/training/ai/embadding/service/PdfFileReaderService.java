package com.training.ai.embadding.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfFileReaderService {
    private final VectorStore vectorStore;

    public void addResource(Resource resource) {
        ExtractedTextFormatter textFormatter = ExtractedTextFormatter.builder()
                .withNumberOfBottomTextLinesToDelete(3)
                .withNumberOfTopPagesToSkipBeforeDelete(1)
                .build();
        PdfDocumentReaderConfig pdfDocumentReaderConfig = PdfDocumentReaderConfig.builder()
                .withPageExtractedTextFormatter(textFormatter)
                .withPagesPerDocument(1)
                .build();

        PagePdfDocumentReader pagePdfDocumentReader =
                new PagePdfDocumentReader(resource, pdfDocumentReaderConfig);

        TokenTextSplitter textSplitter = new TokenTextSplitter();
        vectorStore.accept(textSplitter.apply(pagePdfDocumentReader.get()));
    }

    public List<Document> getResources(String query) {
        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .topK(5)
                .build();
        return vectorStore.similaritySearch(searchRequest);
    }
}
