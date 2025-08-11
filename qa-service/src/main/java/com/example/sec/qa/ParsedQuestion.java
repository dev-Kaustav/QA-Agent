package com.example.sec.qa;

import java.util.Set;

/**
 * Parsed representation of a user question.
 */
public record ParsedQuestion(Set<String> tickers, Set<Integer> years, Set<String> formTypes, String remainder) {
}
