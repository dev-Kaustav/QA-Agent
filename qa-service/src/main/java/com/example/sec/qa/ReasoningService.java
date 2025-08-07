package com.example.sec.qa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import opennlp.tools.tokenize.SimpleTokenizer;
import org.springframework.stereotype.Service;

/**
 * Performs reasoning over retrieved documents using a simple vector space model.
 * The service caches answers to avoid recomputation and exposes a batch API
 * for processing multiple questions.
 */
@Service
public class ReasoningService {

    private static final String FALLBACK = "No relevant answer found.";
    private final SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
    private final Map<String, Answer> cache = new ConcurrentHashMap<>();

    /**
     * Generates an answer for the given question and supporting documents.
     *
     * @param question   user question
     * @param documents  retrieved documents
     * @return answer with confidence score
     */
    public Answer generateAnswer(String question, List<String> documents) {
        if (question == null) {
            question = "";
        }
        Answer cached = cache.get(question);
        if (cached != null) {
            return cached;
        }
        if (documents == null || documents.isEmpty()) {
            Answer result = new Answer(FALLBACK, Collections.emptyList(), 0.0);
            cache.put(question, result);
            return result;
        }
        double bestScore = 0.0;
        String bestSentence = null;
        for (String doc : documents) {
            if (doc == null) {
                continue;
            }
            String[] sentences = doc.split("(?<=[.?!])\\s+");
            for (String sentence : sentences) {
                double score = similarity(question, sentence);
                if (score > bestScore) {
                    bestScore = score;
                    bestSentence = sentence.trim();
                }
            }
        }
        Answer result;
        if (bestSentence != null && bestScore > 0.0) {
            result = new Answer(bestSentence, documents, bestScore);
        } else {
            result = new Answer(FALLBACK, documents, 0.0);
        }
        cache.put(question, result);
        return result;
    }

    /**
     * Batch version of {@link #generateAnswer} for reduced overhead.
     */
    public List<Answer> generateAnswers(List<String> questions, List<List<String>> documentsList) {
        List<Answer> results = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            List<String> docs = documentsList != null && i < documentsList.size()
                ? documentsList.get(i)
                : Collections.emptyList();
            results.add(generateAnswer(questions.get(i), docs));
        }
        return results;
    }

    private double similarity(String a, String b) {
        Map<String, Integer> freqA = frequencies(a);
        Map<String, Integer> freqB = frequencies(b);
        if (freqA.isEmpty() || freqB.isEmpty()) {
            return 0.0;
        }
        Set<String> all = new HashSet<>();
        all.addAll(freqA.keySet());
        all.addAll(freqB.keySet());
        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (String token : all) {
            int x = freqA.getOrDefault(token, 0);
            int y = freqB.getOrDefault(token, 0);
            dot += x * y;
        }
        for (int x : freqA.values()) {
            normA += x * x;
        }
        for (int y : freqB.values()) {
            normB += y * y;
        }
        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private Map<String, Integer> frequencies(String text) {
        String[] tokens = tokenizer.tokenize(text.toLowerCase(Locale.ROOT));
        Map<String, Integer> freq = new HashMap<>();
        for (String token : tokens) {
            if (token.matches("\\w+")) {
                freq.merge(token, 1, Integer::sum);
            }
        }
        return freq;
    }
}
