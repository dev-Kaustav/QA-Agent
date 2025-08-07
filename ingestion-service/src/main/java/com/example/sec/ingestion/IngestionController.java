package com.example.sec.ingestion;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IngestionController {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;

    public IngestionController(KafkaTemplate<String, String> kafkaTemplate,
                               @Value("${kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @PostMapping("/ingest")
    public void ingest(@RequestBody String content) {
        kafkaTemplate.send(topic, content);
    }
}
