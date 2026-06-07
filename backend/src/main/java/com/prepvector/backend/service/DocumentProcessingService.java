package com.prepvector.backend.service;

import com.prepvector.backend.model.Document;
import com.prepvector.backend.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class DocumentProcessingService {

    private final DocumentRepository documentRepository;
    private final Tika tika = new Tika();

    public Document extractText(Long documentId) throws IOException {
        // Find document in DB
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        // Update status to PROCESSING
        document.setStatus("PROCESSING");
        documentRepository.save(document);

        try {
            // Extract text using Apache Tika
            File file = new File(document.getFilePath());
            String extractedText = tika.parseToString(file);

            // Save extracted text to DB
            document.setExtractedText(extractedText);
            document.setStatus("PROCESSED");
            documentRepository.save(document);

        } catch (Exception e) {
            document.setStatus("FAILED");
            documentRepository.save(document);
            throw new RuntimeException("Text extraction failed: " + e.getMessage());
        }

        return document;
    }
}