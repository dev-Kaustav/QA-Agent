package com.example.sec.retrieval;

import com.example.sec.retrieval.model.Section;
import com.example.sec.retrieval.repository.SectionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RetrievalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SectionRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        repository.deleteAll();
        Section s1 = new Section();
        s1.setContent("Revenue was $5 million in 2023.");
        s1.setFilingDate("2023-01-01");
        repository.save(s1);
        Section s2 = new Section();
        s2.setContent("Operating expenses decreased.");
        s2.setFilingDate("2023-01-02");
        repository.save(s2);
    }

    @Test
    void searchReturnsMatchingSections() throws Exception {
        mockMvc.perform(get("/search").param("query", "revenue"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0]").value("Revenue was $5 million in 2023."));
    }

    @Test
    void saveSectionPersistsMetadata() throws Exception {
        Section section = new Section();
        section.setCik("1234");
        section.setType("10-K");
        section.setContent("Test content");
        section.setFilingDate("2023-01-03");

        mockMvc.perform(
                post("/sections")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(section)))
            .andExpect(status().isOk());

        Optional<Section> saved = repository.findAll().stream()
            .filter(s -> "Test content".equals(s.getContent()))
            .findFirst();

        assertThat(saved).isPresent();
        assertThat(saved.get().getCik()).isEqualTo("1234");
        assertThat(saved.get().getType()).isEqualTo("10-K");
        assertThat(saved.get().getFilingDate()).isEqualTo("2023-01-03");
    }
}
