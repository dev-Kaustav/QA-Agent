package com.example.sec.ingestion.service;

import com.example.sec.ingestion.model.Filing;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IngestionServiceImpl implements IngestionService {
  private final MinioClient minioClient;
  private final RestTemplate restTemplate;
  private final KafkaTemplate<String, String> kafkaTemplate;

  @Value("${minio.bucket}")
  private String bucket;

  @Value("${parser.base-url:http://localhost:8082}")
  private String parserBaseUrl;

  @Value("${kafka.topic}")
  private String topic;

  public IngestionServiceImpl(
      MinioClient minioClient,
      RestTemplate restTemplate,
      KafkaTemplate<String, String> kafkaTemplate) {
    this.minioClient = minioClient;
    this.restTemplate = restTemplate;
    this.kafkaTemplate = kafkaTemplate;
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

      Map<?, ?> parsed =
          restTemplate.postForObject(
              parserBaseUrl + "/parse?key=" + objectName, null, Map.class);
      if (parsed != null) {
        Object textObj = parsed.get("text");
        if (textObj instanceof String text && !text.isEmpty()) {
          kafkaTemplate.send(topic, text);
        }
        restTemplate.postForObject(
            retrievalBaseUrl + "/sections", Map.of("content", trimmed), String.class);
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to ingest filing", e);
    }
  }
}
