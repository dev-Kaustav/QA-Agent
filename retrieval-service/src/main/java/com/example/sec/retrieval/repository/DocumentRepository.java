package com.example.sec.retrieval.repository;

import com.example.sec.retrieval.model.Document;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByContentContainingIgnoreCase(String query);
}
