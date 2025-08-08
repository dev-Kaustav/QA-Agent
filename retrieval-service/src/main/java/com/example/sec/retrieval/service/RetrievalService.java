package com.example.sec.retrieval.service;

import java.util.List;
import com.example.sec.retrieval.model.Section;

public interface RetrievalService {
  List<Section> search(String query, String cik, String formType, String filingDate);
}
