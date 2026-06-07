package com.prepvector.backend.service;

import com.prepvector.backend.model.Document;
import com.prepvector.backend.repository.ChunkRepository;
import com.prepvector.backend.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class DeleteService {

    private final DocumentRepository documentRepository;
    private final ChunkRepository chunkRepository;

    @Transactional
    public String deleteDocument(Long documentId) {
        // Find document
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        // Step 1 - Delete file from uploads/ folder
        try {
            Files.deleteIfExists(Paths.get(document.getFilePath()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + e.getMessage());
        }

        // Step 2 - Delete chunks from DB
        chunkRepository.deleteByDocumentId(documentId);

        // Step 3 - Delete document metadata from DB
        documentRepository.deleteById(documentId);

        // Note: Qdrant embeddings will be deleted here on Day 9
        // when we set up the vector DB

        return "Document and all associated data deleted successfully";
    }
}