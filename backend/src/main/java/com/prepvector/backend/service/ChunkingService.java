package com.prepvector.backend.service;

import com.prepvector.backend.model.Chunk;
import com.prepvector.backend.model.Document;
import com.prepvector.backend.repository.ChunkRepository;
import com.prepvector.backend.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChunkingService {

    private final ChunkRepository chunkRepository;
    private final DocumentRepository documentRepository;

    private static final int CHUNK_SIZE = 500;
    private static final int OVERLAP = 50;

    public List<Chunk> chunkDocument(Long documentId) {
        // Get document
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        // Get extracted text
        String text = document.getExtractedText();
        if (text == null || text.isEmpty()) {
            throw new RuntimeException("No extracted text found. Process document first.");
        }

        // Split into words
        String[] words = text.split("\\s+");

        List<Chunk> chunks = new ArrayList<>();
        int chunkIndex = 0;
        int start = 0;

        while (start < words.length) {
            // Get end index
            int end = Math.min(start + CHUNK_SIZE, words.length);

            // Join words back into text
            String chunkContent = String.join(" ",
                    Arrays.copyOfRange(words, start, end));

            // Create chunk
            Chunk chunk = new Chunk();
            chunk.setDocumentId(documentId);
            chunk.setContent(chunkContent);
            chunk.setChunkIndex(chunkIndex);
            chunk.setWordCount(end - start);

            chunks.add(chunk);
            chunkIndex++;

            // Move forward with overlap
            start += CHUNK_SIZE - OVERLAP;
        }

        // Save all chunks
        return chunkRepository.saveAll(chunks);
    }
}