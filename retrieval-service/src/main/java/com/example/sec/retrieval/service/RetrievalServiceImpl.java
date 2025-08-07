package com.example.sec.retrieval.service;

import com.example.sec.retrieval.model.Section;
import com.example.sec.retrieval.repository.SectionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

@Service
public class RetrievalServiceImpl implements RetrievalService {
  private final SectionRepository repository;
  private final RestHighLevelClient esClient;
  private final ObjectMapper objectMapper;

  public RetrievalServiceImpl(
      SectionRepository repository, RestHighLevelClient esClient, ObjectMapper objectMapper) {
    this.repository = repository;
    this.esClient = esClient;
    this.objectMapper = objectMapper;
  }

  @Override
  public List<Section> search(String query) {
    SearchRequest request = new SearchRequest("sections");
    SearchSourceBuilder sourceBuilder =
        new SearchSourceBuilder().query(QueryBuilders.queryStringQuery(query));
    request.source(sourceBuilder);
    try {
      SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);
      return Arrays.stream(response.getHits().getHits())
          .map(SearchHit::getSourceAsString)
          .map(
              json -> {
                try {
                  return objectMapper.readValue(json, Section.class);
                } catch (IOException e) {
                  return null;
                }
              })
          .filter(s -> s != null)
          .collect(Collectors.toList());
    } catch (Exception e) {
      // Fallback to the relational database search when Elasticsearch is unavailable
      return repository.findByContentContainingIgnoreCase(query);
    }
  }
}
