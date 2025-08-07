package com.example.sec.ingestion.service;

import com.example.sec.ingestion.model.Filing;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IngestionServiceImpl implements IngestionService {
  private final MinioClient minioClient;
  private final RestTemplate restTemplate;

  @Value("${minio.bucket}")
  private String bucket;

  @Value("${retrieval.base-url:http://localhost:8085}")
  private String retrievalBaseUrl;

  public IngestionServiceImpl(MinioClient minioClient, RestTemplate restTemplate) {
    this.minioClient = minioClient;
    this.restTemplate = restTemplate;
  }

  @Override
  public void ingest(Filing filing) {
    try {
      String html = restTemplate.getForObject(filing.getUrl(), String.class);
      if (html == null) {
        return;
      }

      String objectName =
          filing.getCik() + "_" + filing.getFormType() + "_" + filing.getFilingDate() + ".html";
      byte[] data = html.getBytes(StandardCharsets.UTF_8);
      minioClient.putObject(
          PutObjectArgs.builder()
              .bucket(bucket)
              .object(objectName)
              .stream(new ByteArrayInputStream(data), data.length, -1)
              .contentType("text/html")
              .build());

      String text = Jsoup.parse(html).text();
      String[] chunks = text.split("(?<=\\.)\\s+");
      for (String chunk : chunks) {
        String trimmed = chunk.trim();
        if (trimmed.isEmpty()) {
          continue;
        }
        restTemplate.postForObject(
            retrievalBaseUrl + "/sections", Map.of("content", trimmed), String.class);
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to ingest filing", e);
    }
  }
}
