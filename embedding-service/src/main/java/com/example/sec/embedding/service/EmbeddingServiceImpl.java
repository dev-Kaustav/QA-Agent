package com.example.sec.embedding.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Service;
import com.example.sec.embedding.model.EmbeddingRecord;
import com.example.sec.embedding.repository.EmbeddingRepository;
import opennlp.tools.tokenize.SimpleTokenizer;

@Service
public class EmbeddingServiceImpl implements EmbeddingService {
  private final EmbeddingRepository repository;
  private final SimpleTokenizer tokenizer;
  private final RestHighLevelClient esClient;

  public EmbeddingServiceImpl(EmbeddingRepository repository,
                              SimpleTokenizer tokenizer,
                              RestHighLevelClient esClient) {
    this.repository = repository;
    this.tokenizer = tokenizer;
    this.esClient = esClient;
  }

  @Override
  public EmbeddingRecord createEmbedding(Long sectionId, String content) {
    String[] tokens = tokenizer.tokenize(content);
    String vectorId = Integer.toHexString(Arrays.hashCode(tokens));
    EmbeddingRecord record = new EmbeddingRecord();
    record.setSectionId(sectionId);
    record.setVectorId(vectorId);

    try {
      Map<String, Object> jsonMap = new HashMap<>();
      jsonMap.put("sectionId", sectionId);
      jsonMap.put("vectorId", vectorId);
      IndexRequest request = new IndexRequest("sections")
          .id(sectionId + ":" + vectorId)
          .source(jsonMap);
      esClient.index(request, RequestOptions.DEFAULT);
    } catch (IOException e) {
      // Log and continue saving to repository as fallback
    }

    return repository.save(record);
  }
}
