package ru.practicum.statsserver.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.statsdto.EndpointHitDto;
import ru.practicum.statsserver.model.EndpointHit;

import java.time.LocalDateTime;

public class EndpointHitMapperTest {

    @Test
    public void testToEntity() {
        EndpointHitDto dto = EndpointHitDto.builder()
                .app("testApp")
                .ip("127.0.0.1")
                .uri("/api/endpoint")
                .timestamp(LocalDateTime.now())
                .build();

        EndpointHit entity = EndpointHitMapper.toEntity(dto);

        Assertions.assertEquals(dto.getApp(), entity.getApp());
        Assertions.assertEquals(dto.getIp(), entity.getIp());
        Assertions.assertEquals(dto.getUri(), entity.getUri());
        Assertions.assertEquals(dto.getTimestamp(), entity.getTimestamp());
    }

    @Test
    public void testToDto() {
        EndpointHit entity = EndpointHit.builder()
                .app("testApp")
                .ip("127.0.0.1")
                .uri("/api/endpoint")
                .timestamp(LocalDateTime.now())
                .build();

        EndpointHitDto dto = EndpointHitMapper.toDto(entity);

        Assertions.assertEquals(entity.getApp(), dto.getApp());
        Assertions.assertEquals(entity.getIp(), dto.getIp());
        Assertions.assertEquals(entity.getUri(), dto.getUri());
        Assertions.assertEquals(entity.getTimestamp(), dto.getTimestamp());
    }
}
