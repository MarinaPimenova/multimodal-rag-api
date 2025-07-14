package com.training.ai.embadding.service;

import com.training.ai.embadding.web.WebPageReaderService;

import com.training.ai.exception.UnsupportedFileType;
import com.training.ai.util.FileValidationUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import static com.training.ai.util.FileValidationUtil.*;

@Service
@RequiredArgsConstructor
public class DataLoaderService {

    private final PdfFileReaderService pdfFileReaderService;
    private final WebPageReaderService webPageReaderService;
    private final ImageReaderService imageReaderService;

    // Upload new knowledge context
    public String uploadKnowledge(MultipartFile multipartFile) {
        Assert.notNull(multipartFile, "Upload new knowledge context cannot be null");
        String originalFileName = multipartFile.getOriginalFilename();

        if (!FileValidationUtil.isValidFile(multipartFile) ||
                !isValidMimeType(multipartFile)) {
            throw new UnsupportedFileType("Only PDF and image files are allowed.");
        }

        if (isPdf(multipartFile)) {
            pdfFileReaderService.addResource(multipartFile);
        }

        if (isKnownImage(multipartFile)) {
            imageReaderService.addResource(multipartFile, multipartFile.getOriginalFilename());
        }

        return originalFileName;
    }

    public String loadFromUrl(String url) {
        Assert.hasText(url, "URL must not be empty");
        webPageReaderService.addWebPageContent(url);
        return "Web content ingested from: " + url;
    }

}
