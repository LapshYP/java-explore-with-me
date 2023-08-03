package ru.practicum.statsserver.repo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.statsdto.ViewStatsDto;
import ru.practicum.statsserver.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StatsRepoTest {
    @Autowired
    private StatsRepo statsRepo;
    private ViewStatsDto viewStatsDto;
    private EndpointHit endpointHit;

    @BeforeEach
    void setUp() {
        viewStatsDto = ViewStatsDto.builder()
                .app("wm-main-service")
                .uri("/events/1")
                .hits(1L)
                .build();


        endpointHit = EndpointHit.builder()
                .id(1L)
                .app("wm-main-service")
                .uri("/events/1")
                .ip("192.163.0.1")
                .timestamp(LocalDateTime.now())
                .build();
        statsRepo.save(endpointHit);

    }

    @Test
    void getUniqueViewStats() {
        List<ViewStatsDto> uniqueViewStats = statsRepo.getUniqueViewStats(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), List.of("/events/1"), null);
        assertNotNull(uniqueViewStats);
    }

    @Test
    void getViewStats() {
        List<ViewStatsDto> uniqueViewStats = statsRepo.getViewStats(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), List.of("/events/1"), null);
        assertNotNull(uniqueViewStats);
    }
}