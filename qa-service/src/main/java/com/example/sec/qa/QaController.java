package com.example.sec.qa;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

@RestController
public class QaController {

    private final RestTemplate restTemplate;
    private final ReasoningService reasoningService;

    @Value("${retrieval.base-url:http://localhost:8085}")
    private String retrievalBaseUrl;

    public QaController(RestTemplate restTemplate, ReasoningService reasoningService) {
        this.restTemplate = restTemplate;
        this.reasoningService = reasoningService;
    }

    @GetMapping("/qa")
    public Answer ask(@RequestParam String question) {
        String url = retrievalBaseUrl + "/search?query=" + UriUtils.encode(question, StandardCharsets.UTF_8);
        List<String> docs = restTemplate.getForObject(url, List.class);
        if (docs == null) {
            docs = Collections.emptyList();
        }
        return reasoningService.generateAnswer(question, docs);
    }
}
