package com.example.sec.retrieval.controller;

import com.example.sec.retrieval.model.Document;
import com.example.sec.retrieval.repository.DocumentRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RetrievalController {

    private final DocumentRepository repository;

    public RetrievalController(DocumentRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/search")
    public List<String> search(@RequestParam String query) {
        return repository.findByContentContainingIgnoreCase(query).stream()
            .map(Document::getContent)
            .collect(Collectors.toList());
    }
}
