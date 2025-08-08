package com.example.sec.chunker.kafka;

import com.example.sec.chunker.model.Section;
import com.example.sec.chunker.service.ChunkerService;
import org.junit.jupiter.api.Test;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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
            "http://embedding-service:8084",
            "http://retrieval-service:8085"
        );

        listener.handle("First sentence. Second sentence.");

        verify(restTemplate, times(2))
            .postForObject(eq("http://embedding-service:8084/embeddings"), any(MultiValueMap.class), eq(Void.class));
        verify(restTemplate, times(2))
            .postForObject(eq("http://retrieval-service:8085/sections"), any(Section.class), eq(Void.class));
    }
}
