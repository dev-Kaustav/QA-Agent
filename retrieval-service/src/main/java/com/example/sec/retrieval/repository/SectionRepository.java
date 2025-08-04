package com.example.sec.retrieval.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sec.retrieval.model.Section;

public interface SectionRepository extends JpaRepository<Section, Long> {}
