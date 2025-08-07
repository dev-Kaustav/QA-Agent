package com.example.sec.chunker.kafka;

import com.example.sec.chunker.model.Section;
import com.example.sec.chunker.service.ChunkerService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class SectionListener {
    private final ChunkerService chunkerService;
    private final RestTemplate restTemplate;
    private final String embeddingBaseUrl;
    private final String retrievalBaseUrl;

    public SectionListener(ChunkerService chunkerService,
                           RestTemplate restTemplate,
                           @Value("${embedding.base-url:http://embedding-service:8084}") String embeddingBaseUrl,
                           @Value("${retrieval.base-url:http://retrieval-service:8085}") String retrievalBaseUrl) {
        this.chunkerService = chunkerService;
        this.restTemplate = restTemplate;
        this.embeddingBaseUrl = embeddingBaseUrl;
        this.retrievalBaseUrl = retrievalBaseUrl;
    }

    @KafkaListener(topics = "${kafka.topic}")
    public void handle(String content) {
        Section section = new Section();
        section.setContent(content);
        section = chunkerService.save(section);
        restTemplate.postForObject(
                retrievalBaseUrl + "/sections", Map.of("content", section.getContent()), Void.class);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("sectionId", section.getId().toString());
        map.add("content", section.getContent());
        restTemplate.postForObject(embeddingBaseUrl + "/embeddings", map, Void.class);
    }
}
