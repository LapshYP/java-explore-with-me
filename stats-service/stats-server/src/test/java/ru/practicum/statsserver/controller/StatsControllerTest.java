package ru.practicum.statsserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.statsdto.EndpointHitDto;
import ru.practicum.statsdto.ViewStatsDto;
import ru.practicum.statsserver.service.StatsService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StatsControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatsService statsService;

    private EndpointHitDto endpointHitDto;
    private ViewStatsDto viewStatsDto;

    @BeforeEach
    void setUp() {
        endpointHitDto = EndpointHitDto.builder()
                .id(1L)
                .app("wm-main-service")
                .uri("/events/1")
                .ip("192.163.0.1")
                .timestamp(LocalDateTime.now())
                .build();

        viewStatsDto = ViewStatsDto.builder()
                .app("wm-main-service")
                .uri("/events/1")
                .hits(1L)
                .build();
    }

    @Test
    @SneakyThrows
    void saveStats() {
        when(statsService.saveStats(any()))
                .thenReturn(endpointHitDto);

        mockMvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(endpointHitDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uri", is(endpointHitDto.getUri())))
                .andExpect(jsonPath("$.ip", is(endpointHitDto.getIp())));

    }

    @SneakyThrows
    @Test
    void getStats() {
        when(statsService.getStats(any(), any(), any(), anyBoolean()))
                .thenReturn(List.of(viewStatsDto));

        mockMvc.perform(get("/stats")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].app", is(viewStatsDto.getApp())))
                .andExpect(jsonPath("$[0].uri", is(viewStatsDto.getUri())))
                .andExpect(jsonPath("$[0].hits").value(viewStatsDto.getHits()));

    }
}