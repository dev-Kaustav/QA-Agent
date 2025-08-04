package com.example.sec.retrieval.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.sec.retrieval.model.Section;
import com.example.sec.retrieval.service.RetrievalService;

@RestController
public class RetrievalController {
  private final RetrievalService retrievalService;

  public RetrievalController(RetrievalService retrievalService) {
    this.retrievalService = retrievalService;
  }

  @GetMapping("/search")
  public List<Section> search(@RequestParam String q) {
    return retrievalService.search(q);
  }
}
