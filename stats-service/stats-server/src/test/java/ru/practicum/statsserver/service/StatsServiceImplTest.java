package ru.practicum.statsserver.service;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import ru.practicum.statsdto.EndpointHitDto;
import ru.practicum.statsdto.ViewStatsDto;
import ru.practicum.statsserver.mapper.EndpointHitMapper;
import ru.practicum.statsserver.model.EndpointHit;
import ru.practicum.statsserver.repo.StatsRepo;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class StatsServiceImplTest {

    @Mock
    private StatsRepo statsRepo;

    @InjectMocks
    private StatsServiceImpl statsService;

    private ModelMapper modelMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        modelMapper = new ModelMapper();
    }

    @Test
    public void testSaveStats() {
        // Arrange
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .id(1L)
                .app("wm-main-service")
                .uri("/events/1")
                .ip("192.163.0.1")
                .timestamp(LocalDateTime.now())
                .build();

        // Mock repository save method
        EndpointHit endpointHit = EndpointHitMapper.toEntity(endpointHitDto);
        when(statsRepo.save(any(EndpointHit.class))).thenReturn(endpointHit);

        // Act
        EndpointHitDto result = statsService.saveStats(endpointHitDto);

        // Assert
        Assertions.assertEquals(endpointHitDto, result);
    }

    @Test
    public void testGetStats_UniqueTrue() {
        // Arrange
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        List<String> uris = Arrays.asList("/events/1", "/events/2");
        Boolean unique = true;
        PageRequest pageable = PageRequest.of(0, 20);

        // Mock repository method
        List<ViewStatsDto> expectedStats = Arrays.asList(
                new ViewStatsDto("192.163.0.1", "/events/1", 3L),
                new ViewStatsDto("192.163.0.2", "/events/1", 2L)
        );
        when(statsRepo.getUniqueViewStats(start, end, uris, pageable)).thenReturn(expectedStats);

        // Act
        List<ViewStatsDto> result = statsService.getStats(start, end, uris, unique);

        // Assert
        Assertions.assertEquals(expectedStats, result);
    }

    @Test
    public void testGetStats_UniqueFalse() {
        // Arrange
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        List<String> uris = Arrays.asList("/events/1", "/events/2");
        Boolean unique = false;
        PageRequest pageable = PageRequest.of(0, 20);

        // Mock repository method
        List<ViewStatsDto> expectedStats = Arrays.asList(
                new ViewStatsDto("192.163.0.1", "/events/1", 5L),
                new ViewStatsDto("192.163.0.2", "/events/1", 3L)
        );
        when(statsRepo.getViewStats(start, end, uris, pageable)).thenReturn(expectedStats);

        // Act
        List<ViewStatsDto> result = statsService.getStats(start, end, uris, unique);

        // Assert
        Assertions.assertEquals(expectedStats, result);
    }
}