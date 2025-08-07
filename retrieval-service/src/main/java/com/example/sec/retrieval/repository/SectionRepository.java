package com.example.sec.retrieval.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sec.retrieval.model.Section;

public interface SectionRepository extends JpaRepository<Section, Long> {
  List<Section> findByContentContainingIgnoreCase(String query);
}
