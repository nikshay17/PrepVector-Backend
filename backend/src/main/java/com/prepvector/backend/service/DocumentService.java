package com.prepvector.backend.service;

import com.prepvector.backend.model.Document;
import com.prepvector.backend.repository.DocumentRepository;
import com.prepvector.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final FileStorageService fileStorageService;
    private final JwtUtil jwtUtil;

    private static final List<String> ALLOWED_TYPES =
            Arrays.asList("pdf", "txt", "ppt","docx");

    public Document uploadDocument(MultipartFile file, String token) throws IOException {
        // Get email from token
        String email = jwtUtil.extractEmail(token);

        // Validate file type
        String fileExtension = fileStorageService.getFileExtension(
                file.getOriginalFilename()
        );
        if (!ALLOWED_TYPES.contains(fileExtension)) {
            throw new RuntimeException("Only PDF, TXT, DOCX allowed");
        }

        // Save file to server
        String filePath = fileStorageService.saveFile(file);

        // Save metadata to DB
        Document document = new Document();
        document.setFilename(file.getOriginalFilename());
        document.setFileType(fileExtension);
        document.setFilePath(filePath);
        document.setStatus("UPLOADED");

        return documentRepository.save(document);
    }
}