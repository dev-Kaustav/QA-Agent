package com.example.sec.retrieval.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Section {
  @Id
  @GeneratedValue
  private Long id;
  private String cik;
  private String type;
  private String content;
  private String filingDate;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getCik() { return cik; }
  public void setCik(String cik) { this.cik = cik; }
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
  public String getContent() { return content; }
  public void setContent(String content) { this.content = content; }
  public String getFilingDate() { return filingDate; }
  public void setFilingDate(String filingDate) { this.filingDate = filingDate; }
}
