package com.prepvector.backend.controller;

import com.prepvector.backend.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/embeddings")
@RequiredArgsConstructor
public class EmbeddingController {

    private final EmbeddingService embeddingService;

    @PostMapping("/test")
    public ResponseEntity<List<Double>> testEmbedding(@RequestBody String text) {
        List<Double> embedding = embeddingService.generateEmbedding(text);
        return ResponseEntity.ok(embedding);
    }
}