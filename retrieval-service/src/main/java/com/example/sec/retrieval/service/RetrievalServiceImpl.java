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
import org.elasticsearch.index.query.BoolQueryBuilder;
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
  public List<Section> search(String query, String cik, String formType, String filingDate) {
    SearchRequest request = new SearchRequest("sections");
    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery().must(QueryBuilders.queryStringQuery(query));
    if (cik != null) {
      boolQuery.filter(QueryBuilders.termQuery("cik", cik));
    }
    if (formType != null) {
      boolQuery.filter(QueryBuilders.termQuery("type", formType));
    }
    if (filingDate != null) {
      boolQuery.filter(QueryBuilders.termQuery("filingDate", filingDate));
    }
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().query(boolQuery);
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
      return repository.search(query, cik, formType, filingDate);
    }
  }
}
