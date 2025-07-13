package com.training.ai.embadding.service;

import com.training.ai.embadding.web.WebPageReaderService;
import com.training.ai.exception.StorageException;
import com.training.ai.exception.UnsupportedFileType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
public class DataLoaderService {
    private final PdfFileReaderService pdfFileReaderService;
    private final WebPageReaderService webPageReaderService;

    // Upload new knowledge context
    public String uploadKnowledge(MultipartFile multipartFile) {
        Assert.notNull(multipartFile, "Upload new knowledge context cannot be null");
        if (!isSupportedContentType(multipartFile.getContentType())) {
            throw new UnsupportedFileType("Unsupported file type: " + multipartFile.getContentType());
        }

        File f = store(multipartFile);

        FileSystemResource resource = new FileSystemResource(f);
        pdfFileReaderService.addResource(resource);

        return multipartFile.getOriginalFilename();
    }

    public String loadFromUrl(String url) {
        Assert.hasText(url, "URL must not be empty");
        webPageReaderService.addWebPageContent(url);
        return "Web content ingested from: " + url;
    }

    public File store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }
            String uploadDir = "/uploads";
            Path filePath = Paths.get(uploadDir, file.getOriginalFilename());
            Files.createDirectories(filePath.getParent()); // Create directory if it doesn't exist
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return filePath.toFile();
        } catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }
    }

    private boolean isSupportedContentType(String contentType) {
        return !contentType.equals("image/jpeg");
    }
}
