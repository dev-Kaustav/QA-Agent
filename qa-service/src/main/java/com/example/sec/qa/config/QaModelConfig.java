package com.example.sec.qa.config;

import opennlp.tools.tokenize.SimpleTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QaModelConfig {
  @Bean
  public SimpleTokenizer tokenizer() {
    return SimpleTokenizer.INSTANCE;
  }
}
