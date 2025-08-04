package com.example.sec.chunker.service;

import org.springframework.stereotype.Service;
import com.example.sec.chunker.model.Section;
import com.example.sec.chunker.repository.SectionRepository;

@Service
public class ChunkerServiceImpl implements ChunkerService {
  private final SectionRepository repository;

  public ChunkerServiceImpl(SectionRepository repository) {
    this.repository = repository;
  }

  @Override
  public Section save(Section section) {
    return repository.save(section);
  }
}
