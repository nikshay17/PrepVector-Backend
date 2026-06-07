package com.prepvector.backend.repository;

import com.prepvector.backend.model.Chunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChunkRepository extends JpaRepository<Chunk, Long> {
    List<Chunk> findByDocumentId(Long documentId);
    void deleteByDocumentId(Long documentId);
}