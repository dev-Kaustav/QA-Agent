package com.example.sec.retrieval;

import com.example.sec.retrieval.model.Document;
import com.example.sec.retrieval.repository.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RetrievalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DocumentRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll();
        repository.save(new Document(null, "Revenue was $5 million in 2023."));
        repository.save(new Document(null, "Operating expenses decreased."));
    }

    @Test
    void searchReturnsMatchingDocuments() throws Exception {
        mockMvc.perform(get("/search").param("query", "revenue"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0]").value("Revenue was $5 million in 2023."));
    }
}
