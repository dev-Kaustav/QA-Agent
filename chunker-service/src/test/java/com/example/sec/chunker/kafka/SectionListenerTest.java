package com.example.sec.chunker.kafka;

import com.example.sec.chunker.model.Section;
import com.example.sec.chunker.service.ChunkerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SectionListenerTest {

    @Test
    void handle_postsSectionsToRetrievalService() {
        ChunkerService chunkerService = mock(ChunkerService.class);
        RestTemplate restTemplate = mock(RestTemplate.class);
        when(chunkerService.save(any(Section.class))).thenAnswer(invocation -> {
            Section s = invocation.getArgument(0);
            s.setId(1L);
            return s;
        });

        SectionListener listener = new SectionListener(
            chunkerService,
            restTemplate,
            new ObjectMapper(),
            "http://embedding-service:8084",
            "http://retrieval-service:8085"
        );

        String json = "{" +
            "\"cik\":\"0001\"," +
            "\"formType\":\"10-K\"," +
            "\"filingDate\":\"2023-01-01\"," +
            "\"text\":\"First sentence. Second sentence.\"" +
            "}";
        listener.handle(json);

        ArgumentCaptor<MultiValueMap<String, String>> embedCaptor = ArgumentCaptor.forClass(MultiValueMap.class);
        ArgumentCaptor<Section> sectionCaptor = ArgumentCaptor.forClass(Section.class);

        verify(restTemplate, times(2))
            .postForObject(eq("http://embedding-service:8084/embeddings"), embedCaptor.capture(), eq(Void.class));
        verify(restTemplate, times(2))
            .postForObject(eq("http://retrieval-service:8085/sections"), sectionCaptor.capture(), eq(Void.class));

        List<MultiValueMap<String, String>> embedMaps = embedCaptor.getAllValues();
        assertThat(embedMaps.get(0).getFirst("content")).isEqualTo("First sentence.");

        List<Section> sections = sectionCaptor.getAllValues();
        assertThat(sections.get(0).getCik()).isEqualTo("0001");
        assertThat(sections.get(0).getType()).isEqualTo("10-K");
    }
}
