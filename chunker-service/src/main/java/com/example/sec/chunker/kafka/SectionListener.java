package com.example.sec.chunker.kafka;

import com.example.sec.chunker.model.Section;
import com.example.sec.chunker.service.ChunkerService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;
    private final String embeddingBaseUrl;
    private final String retrievalBaseUrl;

    public SectionListener(
            ChunkerService chunkerService,
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            @Value("${embedding.base-url:http://embedding-service:8084}") String embeddingBaseUrl,
            @Value("${retrieval.base-url:http://retrieval-service:8085}") String retrievalBaseUrl) {
        this.chunkerService = chunkerService;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.embeddingBaseUrl = embeddingBaseUrl;
        this.retrievalBaseUrl = retrievalBaseUrl;
    }

    @KafkaListener(topics = "${kafka.topic}")
    public void handle(String payload) {
        try {
            Map<?, ?> parsed = objectMapper.readValue(payload, Map.class);
            Object textObj = parsed.get("text");
            if (!(textObj instanceof String text) || text.isEmpty()) {
                return;
            }
            String cik = (String) parsed.get("cik");
            String formType = (String) parsed.get("formType");
            String[] chunks = text.split("(?<=\\.)\\s+");
            for (String chunk : chunks) {
                String trimmed = chunk.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                Section section = new Section();
                section.setContent(trimmed);
                section.setCik(cik);
                section.setType(formType);
                section = chunkerService.save(section);
                MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
                map.add("sectionId", section.getId().toString());
                map.add("content", section.getContent());
                restTemplate.postForObject(embeddingBaseUrl + "/embeddings", map, Void.class);
                restTemplate.postForObject(retrievalBaseUrl + "/sections", section, Void.class);
            }
        } catch (Exception e) {
            // ignore malformed messages
        }
    }
}
