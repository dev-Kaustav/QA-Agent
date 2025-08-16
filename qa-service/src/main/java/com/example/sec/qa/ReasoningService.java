package com.example.sec.qa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    public Answer generateAnswer(String question, List<Section> documents) {
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
        String bestSentence = bestSentence(question, documents);
        Answer result;
        if (bestSentence != null) {
            double confidence = similarity(question, bestSentence);
            result = new Answer(bestSentence, documents, confidence);
        } else {
            result = new Answer(FALLBACK, documents, 0.0);
        }
        cache.put(question, result);
        return result;
    }

    /**
     * Generates answers for each document group and returns a comparative
     * response.
     */
    public Answer generateComparativeAnswer(String question, Map<String, List<String>> documentsByGroup) {
        Map<String, String> bestByGroup = new LinkedHashMap<>();
        List<String> allDocs = new ArrayList<>();
        for (Map.Entry<String, List<String>> e : documentsByGroup.entrySet()) {
            List<String> docs = e.getValue();
            if (docs != null) {
                allDocs.addAll(docs);
            }
            String best = bestSentence(question, docs);
            if (best != null) {
                bestByGroup.put(e.getKey(), best);
            }
        }
        if (bestByGroup.isEmpty()) {
            return new Answer(FALLBACK, allDocs, 0.0);
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> e : bestByGroup.entrySet()) {
            sb.append(e.getKey()).append(": ").append(e.getValue()).append("\n");
        }
        String comparison = compareNumerical(bestByGroup);
        if (!comparison.isEmpty()) {
            sb.append(comparison);
        }
        return new Answer(sb.toString().trim(), allDocs, 1.0);
    }

    /**
     * Batch version of {@link #generateAnswer} for reduced overhead.
     */
    public List<Answer> generateAnswers(List<String> questions, List<List<Section>> documentsList) {
        List<Answer> results = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            List<Section> docs = documentsList != null && i < documentsList.size()
                ? documentsList.get(i)
                : Collections.emptyList();
            results.add(generateAnswer(questions.get(i), docs));
        }
        return results;
    }

    private String bestSentence(String question, List<String> documents) {
        double bestScore = 0.0;
        String bestSentence = null;
        if (documents == null) {
            return null;
        }
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
        return bestSentence;
    }

    private String compareNumerical(Map<String, String> bestByGroup) {
        Map<String, Double> values = new LinkedHashMap<>();
        Pattern number = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+");
        for (Map.Entry<String, String> e : bestByGroup.entrySet()) {
            Matcher m = number.matcher(e.getValue().replaceAll(",", ""));
            if (m.find()) {
                try {
                    values.put(e.getKey(), Double.parseDouble(m.group()));
                } catch (NumberFormatException ignore) {
                }
            }
        }
        if (values.size() < 2) {
            return "";
        }
        String maxKey = Collections.max(values.entrySet(), Map.Entry.comparingByValue()).getKey();
        String minKey = Collections.min(values.entrySet(), Map.Entry.comparingByValue()).getKey();
        if (values.get(maxKey).equals(values.get(minKey))) {
            return "All values are equal.";
        }
        return maxKey + " is highest while " + minKey + " is lowest.";
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
