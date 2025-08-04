package com.example.sec.retrieval.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {
  @Bean
  public RestHighLevelClient esClient(@Value("${elasticsearch.host}") String host) {
    return new RestHighLevelClient(RestClient.builder(HttpHost.create(host)));
  }
}
