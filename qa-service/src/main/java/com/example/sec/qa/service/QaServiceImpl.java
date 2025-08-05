package com.example.sec.qa.service;

import org.springframework.stereotype.Service;
import com.example.sec.qa.model.QaRequest;
import com.example.sec.qa.model.QaResponse;
import opennlp.tools.tokenize.SimpleTokenizer;

@Service
public class QaServiceImpl implements QaService {
  private final SimpleTokenizer tokenizer;

  public QaServiceImpl(SimpleTokenizer tokenizer) {
    this.tokenizer = tokenizer;
  }

  @Override
  public QaResponse answer(QaRequest request) {
    String[] tokens = tokenizer.tokenize(request.getQuestion());
    return new QaResponse(String.join(" ", tokens));
  }
}
