package com.example.sec.parser.service;

import org.springframework.stereotype.Service;
import com.example.sec.parser.model.ParsedDocument;
import io.minio.MinioClient;

@Service
public class ParserServiceImpl implements ParserService {
  private final MinioClient minioClient;

  public ParserServiceImpl(MinioClient minioClient) {
    this.minioClient = minioClient;
  }

  @Override
  public ParsedDocument parse(String key) {
    // placeholder: read HTML from MinIO and parse
    return new ParsedDocument(key, "parsed text");
  }
}
