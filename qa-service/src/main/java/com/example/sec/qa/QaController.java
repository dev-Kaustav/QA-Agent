package com.example.sec.qa;

import java.util.ArrayList;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    private final QuestionParser questionParser;

    @Value("${retrieval.base-url:http://localhost:8085}")
    private String retrievalBaseUrl;

    public QaController(RestTemplate restTemplate, ReasoningService reasoningService, QuestionParser questionParser) {
        this.restTemplate = restTemplate;
        this.reasoningService = reasoningService;
        this.questionParser = questionParser;
    }

    @GetMapping("/qa")
    public Answer ask(@RequestParam String question) {
        ParsedQuestion parsed = questionParser.parse(question);
        Map<String, List<String>> docsByGroup = new LinkedHashMap<>();

        List<String> tickers = parsed.tickers().isEmpty() ? List.of("") : new ArrayList<>(parsed.tickers());
        List<Integer> years = parsed.years().isEmpty() ? List.of((Integer) null) : new ArrayList<>(parsed.years());
        List<String> forms = parsed.formTypes().isEmpty() ? List.of("") : new ArrayList<>(parsed.formTypes());

        for (String ticker : tickers) {
            for (Integer year : years) {
                for (String form : forms) {
                    UriComponentsBuilder builder =
                        UriComponentsBuilder.fromHttpUrl(retrievalBaseUrl + "/search")
                            .queryParam("query", parsed.remainder());
                    if (ticker != null && !ticker.isBlank()) {
                        builder.queryParam("ticker", ticker);
                    }
                    if (year != null) {
                        builder.queryParam("year", year);
                    }
                    if (form != null && !form.isBlank()) {
                        builder.queryParam("formType", form);
                    }
                    String url = builder.encode().toUriString();
                    List<String> docs = restTemplate.getForObject(url, List.class);
                    if (docs == null) {
                        docs = Collections.emptyList();
                    }
                    String label = ((ticker != null ? ticker : "") + " " +
                                    (year != null ? year : "") + " " +
                                    (form != null ? form : "")).trim();
                    docsByGroup.put(label.isEmpty() ? "default" : label, docs);
                }
            }
        }

        if (docsByGroup.size() <= 1) {
            List<String> docs = docsByGroup.values().stream().findFirst().orElse(Collections.emptyList());
            return reasoningService.generateAnswer(parsed.remainder(), docs);
        }
        return reasoningService.generateComparativeAnswer(parsed.remainder(), docsByGroup);
    }
}
