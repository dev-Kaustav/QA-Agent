package com.example.sec.retrieval.service;

import java.util.List;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Service;
import com.example.sec.retrieval.model.Section;
import com.example.sec.retrieval.repository.SectionRepository;

@Service
public class RetrievalServiceImpl implements RetrievalService {
  private final SectionRepository repository;
  private final RestHighLevelClient esClient;

  public RetrievalServiceImpl(SectionRepository repository, RestHighLevelClient esClient) {
    this.repository = repository;
    this.esClient = esClient;
  }

  @Override
  public List<Section> search(String query) {
    // placeholder: query vector DB and metadata DB
    return repository.findAll();
  }
}
