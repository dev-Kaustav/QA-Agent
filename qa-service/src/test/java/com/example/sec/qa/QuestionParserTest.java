package com.example.sec.qa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.Test;

class QuestionParserTest {

    private final QuestionParser parser = new QuestionParser();

    @Test
    void extractsMetadata() {
        String q = "Compare revenue for AAPL and MSFT in 2020 10-K";
        ParsedQuestion pq = parser.parse(q);
        assertEquals(Set.of("AAPL", "MSFT"), pq.tickers());
        assertEquals(Set.of(2020), pq.years());
        assertEquals(Set.of("10-K"), pq.formTypes());
        assertTrue(pq.remainder().toLowerCase().contains("compare revenue"));
    }
}
