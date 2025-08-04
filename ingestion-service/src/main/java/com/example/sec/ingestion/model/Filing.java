package com.example.sec.ingestion.model;

public class Filing {
  private String cik;
  private String formType;
  private String filingDate;
  private String url;

  public Filing() {}

  public Filing(String cik, String formType, String filingDate, String url) {
    this.cik = cik;
    this.formType = formType;
    this.filingDate = filingDate;
    this.url = url;
  }

  public String getCik() { return cik; }
  public void setCik(String cik) { this.cik = cik; }
  public String getFormType() { return formType; }
  public void setFormType(String formType) { this.formType = formType; }
  public String getFilingDate() { return filingDate; }
  public void setFilingDate(String filingDate) { this.filingDate = filingDate; }
  public String getUrl() { return url; }
  public void setUrl(String url) { this.url = url; }
}
