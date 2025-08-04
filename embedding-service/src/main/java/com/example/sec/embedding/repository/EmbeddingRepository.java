package com.example.sec.embedding.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sec.embedding.model.EmbeddingRecord;

public interface EmbeddingRepository extends JpaRepository<EmbeddingRecord, Long> {}
