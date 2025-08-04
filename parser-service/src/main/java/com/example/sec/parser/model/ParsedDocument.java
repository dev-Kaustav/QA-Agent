package com.example.sec.parser.model;

public class ParsedDocument {
  private String cik;
  private String text;

  public ParsedDocument() {}

  public ParsedDocument(String cik, String text) {
    this.cik = cik;
    this.text = text;
  }

  public String getCik() { return cik; }
  public void setCik(String cik) { this.cik = cik; }
  public String getText() { return text; }
  public void setText(String text) { this.text = text; }
}
