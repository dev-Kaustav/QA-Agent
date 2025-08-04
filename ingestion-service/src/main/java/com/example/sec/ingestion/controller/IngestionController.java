package com.example.sec.ingestion.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.sec.ingestion.model.Filing;
import com.example.sec.ingestion.service.IngestionService;

@RestController
public class IngestionController {
  private final IngestionService ingestionService;

  public IngestionController(IngestionService ingestionService) {
    this.ingestionService = ingestionService;
  }

  @PostMapping("/ingest")
  public void ingest(@RequestBody Filing filing) {
    ingestionService.ingest(filing);
  }
}
