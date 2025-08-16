package com.example.sec.qa;

import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReasoningServiceTest {

    private final ReasoningService reasoningService = new ReasoningService();

    @Test
    void findsSentenceContainingQuestionToken() {
        String question = "What was the revenue?";
        List<String> docs = List.of("Revenue was $5 million. Net income increased.");
        Answer answer = reasoningService.generateAnswer(question, docs);
        assertEquals("Revenue was $5 million.", answer.answer());
        assertTrue(answer.confidence() > 0.0);
    }

    @Test
    void returnsFallbackWhenNoMatch() {
        String question = "What is the market cap?";
        List<String> docs = List.of("No relevant data is present.");
        Answer answer = reasoningService.generateAnswer(question, docs);
        assertEquals("No relevant answer found.", answer.answer());
        assertEquals(0.0, answer.confidence());
    }

    @Test
    void aggregatesAndComparesAcrossGroups() {
        var docsByGroup = new java.util.LinkedHashMap<String, List<String>>();
        docsByGroup.put("AAPL 2020", List.of("Revenue was $5 million."));
        docsByGroup.put("AAPL 2021", List.of("Revenue was $6 million."));
        Answer answer = reasoningService.generateComparativeAnswer("What was the revenue?", docsByGroup);
        assertTrue(answer.answer().contains("AAPL 2020: Revenue was $5 million."));
        assertTrue(answer.answer().contains("AAPL 2021: Revenue was $6 million."));
        assertTrue(answer.answer().contains("highest") || answer.answer().contains("All values"));
    }
}
