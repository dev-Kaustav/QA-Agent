package com.example.sec.qa;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
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
    public Answer ask(
            @RequestParam String question,
            @RequestParam(required = false) String cik,
            @RequestParam(required = false) String formType,
            @RequestParam(required = false) String filingDate) {
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(retrievalBaseUrl + "/search")
                        .queryParam("query", UriUtils.encode(question, StandardCharsets.UTF_8))
                        .queryParamIfPresent(
                                "cik",
                                Optional.ofNullable(cik)
                                        .map(s -> UriUtils.encode(s, StandardCharsets.UTF_8)))
                        .queryParamIfPresent(
                                "formType",
                                Optional.ofNullable(formType)
                                        .map(s -> UriUtils.encode(s, StandardCharsets.UTF_8)))
                        .queryParamIfPresent(
                                "filingDate",
                                Optional.ofNullable(filingDate)
                                        .map(s -> UriUtils.encode(s, StandardCharsets.UTF_8)));
        Section[] response = restTemplate.getForObject(builder.toUriString(), Section[].class);
        List<Section> docs = response == null ? Collections.emptyList() : Arrays.asList(response);
        return reasoningService.generateAnswer(question, docs);
    }
}
