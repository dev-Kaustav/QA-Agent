package com.example.sec.embedding.service;

import com.example.sec.embedding.model.EmbeddingRecord;

public interface EmbeddingService {
  EmbeddingRecord createEmbedding(Long sectionId, String content);
}
