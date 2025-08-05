package com.example.sec.embedding.config;

import opennlp.tools.tokenize.SimpleTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddingModelConfig {
  @Bean
  public SimpleTokenizer tokenizer() {
    return SimpleTokenizer.INSTANCE;
  }
}
