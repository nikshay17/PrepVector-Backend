package com.prepvector.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Data
@NoArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String filename;
    private String fileType;
    private String filePath;
    private String status;

    @Column(columnDefinition = "TEXT")
    private String extractedText;

    private LocalDateTime uploadTime = LocalDateTime.now();
}