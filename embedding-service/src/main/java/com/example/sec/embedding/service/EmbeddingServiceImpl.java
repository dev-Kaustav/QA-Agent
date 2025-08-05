package com.example.sec.embedding.service;

import java.util.Arrays;
import org.springframework.stereotype.Service;
import com.example.sec.embedding.model.EmbeddingRecord;
import com.example.sec.embedding.repository.EmbeddingRepository;
import opennlp.tools.tokenize.SimpleTokenizer;

@Service
public class EmbeddingServiceImpl implements EmbeddingService {
  private final EmbeddingRepository repository;
  private final SimpleTokenizer tokenizer;

  public EmbeddingServiceImpl(EmbeddingRepository repository, SimpleTokenizer tokenizer) {
    this.repository = repository;
    this.tokenizer = tokenizer;
  }

  @Override
  public EmbeddingRecord createEmbedding(Long sectionId, String content) {
    String[] tokens = tokenizer.tokenize(content);
    String vectorId = Integer.toHexString(Arrays.hashCode(tokens));
    EmbeddingRecord record = new EmbeddingRecord();
    record.setSectionId(sectionId);
    record.setVectorId(vectorId);
    return repository.save(record);
  }
}
