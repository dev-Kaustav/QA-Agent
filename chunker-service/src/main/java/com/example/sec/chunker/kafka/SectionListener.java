package com.example.sec.chunker.kafka;

import com.example.sec.chunker.model.Section;
import com.example.sec.chunker.service.ChunkerService;
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

    public SectionListener(ChunkerService chunkerService,
                           RestTemplate restTemplate,
                           @Value("${embedding.base-url}") String embeddingBaseUrl) {
        this.chunkerService = chunkerService;
        this.restTemplate = restTemplate;
        this.embeddingBaseUrl = embeddingBaseUrl;
    }

    @KafkaListener(topics = "${kafka.topic}")
    public void handle(String content) {
        Section section = new Section();
        section.setContent(content);
        section = chunkerService.save(section);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("sectionId", section.getId().toString());
        map.add("content", section.getContent());
        restTemplate.postForObject(embeddingBaseUrl + "/embeddings", map, Void.class);
    }
}
