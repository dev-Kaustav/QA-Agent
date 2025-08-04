package com.example.sec.ingestion.service;

import org.springframework.stereotype.Service;
import com.example.sec.ingestion.model.Filing;
import io.minio.MinioClient;

@Service
public class IngestionServiceImpl implements IngestionService {
  private final MinioClient minioClient;

  public IngestionServiceImpl(MinioClient minioClient) {
    this.minioClient = minioClient;
  }

  @Override
  public void ingest(Filing filing) {
    // placeholder for fetching SEC filing and uploading to MinIO
  }
}
