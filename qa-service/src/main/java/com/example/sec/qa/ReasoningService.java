package com.example.sec.qa;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import opennlp.tools.tokenize.SimpleTokenizer;
import org.springframework.stereotype.Service;

@Service
public class ReasoningService {

    public String generateAnswer(String question, List<String> documents) {
        if (documents == null || documents.isEmpty()) {
            return "No relevant answer found.";
        }
        Set<String> tokens = new HashSet<>(Arrays.asList(SimpleTokenizer.INSTANCE.tokenize(question.toLowerCase())));
        for (String doc : documents) {
            String[] sentences = doc.split("(?<=[.?!])\\s+");
            for (String sentence : sentences) {
                String lower = sentence.toLowerCase();
                for (String token : tokens) {
                    if (lower.contains(token)) {
                        return sentence.trim();
                    }
                }
            }
        }
        return "No relevant answer found.";
    }
}
