package com.example.sec.parser.service;

import com.example.sec.parser.model.ParsedDocument;

public interface ParserService {
  ParsedDocument parse(String key);
}
