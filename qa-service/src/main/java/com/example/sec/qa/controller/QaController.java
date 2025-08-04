package com.example.sec.qa.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.sec.qa.model.QaRequest;
import com.example.sec.qa.model.QaResponse;
import com.example.sec.qa.service.QaService;

@RestController
public class QaController {
  private final QaService qaService;

  public QaController(QaService qaService) {
    this.qaService = qaService;
  }

  @PostMapping("/qa")
  public QaResponse answer(@RequestBody QaRequest request) {
    return qaService.answer(request);
  }
}
