package com.example.sec.qa.service;

import com.example.sec.qa.model.QaRequest;
import com.example.sec.qa.model.QaResponse;

public interface QaService {
  QaResponse answer(QaRequest request);
}
