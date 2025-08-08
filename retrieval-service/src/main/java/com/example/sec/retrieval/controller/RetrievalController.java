package com.example.sec.retrieval.controller;

import com.example.sec.retrieval.model.Section;
import com.example.sec.retrieval.repository.SectionRepository;
import com.example.sec.retrieval.service.RetrievalService;
import java.util.List;
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
    public List<Section> search(
            @RequestParam String query,
            @RequestParam(required = false) String cik,
            @RequestParam(required = false) String formType,
            @RequestParam(required = false) String filingDate) {
        return retrievalService.search(query, cik, formType, filingDate);
    }

    @PostMapping("/sections")
    public Section save(@RequestBody Section section) {
        return repository.save(section);
    }
}
