package com.example.sec.ingestion.service;

import com.example.sec.ingestion.model.Filing;

public interface IngestionService {
  void ingest(Filing filing);
}
