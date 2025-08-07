package com.example.sec.qa;

import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ReasoningServiceTest {

    private final ReasoningService reasoningService = new ReasoningService();

    @Test
    void findsSentenceContainingQuestionToken() {
        String question = "What was the revenue?";
        List<String> docs = List.of("Revenue was $5 million. Net income increased.");
        String answer = reasoningService.generateAnswer(question, docs);
        assertEquals("Revenue was $5 million.", answer);
    }

    @Test
    void returnsFallbackWhenNoMatch() {
        String question = "What is the market cap?";
        List<String> docs = List.of("No relevant data is present.");
        String answer = reasoningService.generateAnswer(question, docs);
        assertEquals("No relevant answer found.", answer);
    }
}
