package com.example.sec.chunker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sec.chunker.model.Section;

public interface SectionRepository extends JpaRepository<Section, Long> {}
