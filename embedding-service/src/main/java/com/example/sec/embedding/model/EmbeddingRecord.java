package com.example.sec.embedding.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class EmbeddingRecord {
  @Id
  @GeneratedValue
  private Long id;
  private Long sectionId;
  private String vectorId;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getSectionId() { return sectionId; }
  public void setSectionId(Long sectionId) { this.sectionId = sectionId; }
  public String getVectorId() { return vectorId; }
  public void setVectorId(String vectorId) { this.vectorId = vectorId; }
}
