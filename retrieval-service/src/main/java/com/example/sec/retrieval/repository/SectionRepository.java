package com.example.sec.retrieval.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.sec.retrieval.model.Section;

public interface SectionRepository extends JpaRepository<Section, Long> {

  @Query(
      "SELECT s FROM Section s WHERE "
          + "LOWER(s.content) LIKE LOWER(CONCAT('%', :query, '%')) "
          + "AND (:cik IS NULL OR s.cik = :cik) "
          + "AND (:formType IS NULL OR s.type = :formType) "
          + "AND (:filingDate IS NULL OR s.filingDate = :filingDate)")
  List<Section> search(
      @Param("query") String query,
      @Param("cik") String cik,
      @Param("formType") String formType,
      @Param("filingDate") String filingDate);
}
