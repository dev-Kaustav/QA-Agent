package com.example.sec.embedding.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.sec.embedding.model.EmbeddingRecord;
import com.example.sec.embedding.service.EmbeddingService;

@RestController
public class EmbeddingController {
  private final EmbeddingService embeddingService;

  public EmbeddingController(EmbeddingService embeddingService) {
    this.embeddingService = embeddingService;
  }

  @PostMapping("/embeddings")
  public EmbeddingRecord create(@RequestParam Long sectionId, @RequestParam String content) {
    return embeddingService.createEmbedding(sectionId, content);
  }
}
