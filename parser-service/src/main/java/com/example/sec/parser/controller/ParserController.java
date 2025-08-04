package com.example.sec.parser.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.sec.parser.model.ParsedDocument;
import com.example.sec.parser.service.ParserService;

@RestController
public class ParserController {
  private final ParserService parserService;

  public ParserController(ParserService parserService) {
    this.parserService = parserService;
  }

  @PostMapping("/parse")
  public ParsedDocument parse(@RequestParam String key) {
    return parserService.parse(key);
  }
}
