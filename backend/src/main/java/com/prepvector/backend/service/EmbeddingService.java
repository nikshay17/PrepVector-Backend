package com.prepvector.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;

@Service
public class EmbeddingService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public EmbeddingService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public List<Double> generateEmbedding(String text) {
        try {
            // Build request body
            Map<String, Object> requestBody = Map.of(
                    "model", "models/gemini-embedding-001",
                    "content", Map.of(
                            "parts", List.of(
                                    Map.of("text", text)
                            )
                    )
            );

            // Call Gemini API
            Map response = webClient.post()
                    .uri("/v1beta/models/gemini-embedding-001:embedContent?key=" + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            // Extract embedding values
            Map embedding = (Map) response.get("embedding");
            List<Double> values = (List<Double>) embedding.get("values");

            return values;

        } catch (Exception e) {
            throw new RuntimeException("Embedding generation failed: " + e.getMessage());
        }
    }
}