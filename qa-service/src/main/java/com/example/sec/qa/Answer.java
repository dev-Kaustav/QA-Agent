package com.example.sec.qa;

import java.util.List;

/**
 * Represents an answer returned by the reasoning service.
 *
 * @param answer     the answer text
 * @param sources    the documents used as evidence
 * @param confidence confidence score between 0 and 1
 */
public record Answer(String answer, List<String> sources, double confidence) {
}
