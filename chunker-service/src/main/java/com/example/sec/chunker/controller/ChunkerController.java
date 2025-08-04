package com.example.sec.chunker.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.sec.chunker.model.Section;
import com.example.sec.chunker.service.ChunkerService;

@RestController
public class ChunkerController {
  private final ChunkerService chunkerService;

  public ChunkerController(ChunkerService chunkerService) {
    this.chunkerService = chunkerService;
  }

  @PostMapping("/sections")
  public Section save(@RequestBody Section section) {
    return chunkerService.save(section);
  }
}
