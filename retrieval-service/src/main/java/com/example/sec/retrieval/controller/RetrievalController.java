package com.example.sec.retrieval.controller;

import com.example.sec.retrieval.model.Section;
import com.example.sec.retrieval.repository.SectionRepository;
import com.example.sec.retrieval.service.RetrievalService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RetrievalController {

    private final SectionRepository repository;
    private final RetrievalService retrievalService;

    public RetrievalController(SectionRepository repository, RetrievalService retrievalService) {
        this.repository = repository;
        this.retrievalService = retrievalService;
    }

    @GetMapping("/search")
    public List<String> search(@RequestParam String query) {
        return retrievalService.search(query).stream()
            .map(Section::getContent)
            .collect(Collectors.toList());
    }

    @PostMapping("/sections")
    public Section save(@RequestBody Section section) {
        return repository.save(section);
    }
}
