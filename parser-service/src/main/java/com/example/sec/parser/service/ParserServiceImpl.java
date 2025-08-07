package com.example.sec.parser.service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.example.sec.parser.model.ParsedDocument;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;

@Service
public class ParserServiceImpl implements ParserService {
  private final MinioClient minioClient;

  @Value("${minio.bucket}")
  private String bucket;

  public ParserServiceImpl(MinioClient minioClient) {
    this.minioClient = minioClient;
  }

  @Override
  public ParsedDocument parse(String key) {
    try (InputStream stream =
        minioClient.getObject(
            GetObjectArgs.builder().bucket(bucket).object(key).build())) {
      String html = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
      String text = Jsoup.parse(html).text();
      return new ParsedDocument(key, text);
    } catch (Exception e) {
      return null;
    }
  }
}
