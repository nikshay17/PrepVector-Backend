package com.prepvector.backend.controller;

import com.prepvector.backend.model.Chunk;
import com.prepvector.backend.model.Document;
import com.prepvector.backend.repository.DocumentRepository;
import com.prepvector.backend.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final DocumentService documentService;
    private final DocumentProcessingService documentProcessingService;
    private final ChunkingService chunkingService;
    private final DeleteService deleteService;
    private final DocumentRepository documentRepository;

    // Upload file
    @PostMapping("/upload")
    public ResponseEntity<Document> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Document document = documentService.uploadDocument(file, token);
            return ResponseEntity.ok(document);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Extract text
    @PostMapping("/process/{documentId}")
    public ResponseEntity<Document> processFile(
            @PathVariable Long documentId) {
        try {
            Document document = documentProcessingService.extractText(documentId);
            return ResponseEntity.ok(document);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Chunk document
    @PostMapping("/chunk/{documentId}")
    public ResponseEntity<List<Chunk>> chunkFile(
            @PathVariable Long documentId) {
        try {
            List<Chunk> chunks = chunkingService.chunkDocument(documentId);
            return ResponseEntity.ok(chunks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Delete file + chunks + embeddings
    @DeleteMapping("/{documentId}")
    public ResponseEntity<String> deleteFile(
            @PathVariable Long documentId) {
        try {
            String response = deleteService.deleteDocument(documentId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // List all documents
    @GetMapping
    public ResponseEntity<List<Document>> getAllFiles() {
        return ResponseEntity.ok(documentRepository.findAll());
    }
}