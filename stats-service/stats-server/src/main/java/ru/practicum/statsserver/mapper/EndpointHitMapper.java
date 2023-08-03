package ru.practicum.statsserver.mapper;


import lombok.experimental.UtilityClass;
import ru.practicum.statsdto.EndpointHitDto;
import ru.practicum.statsserver.model.EndpointHit;


@UtilityClass
public class EndpointHitMapper {

    public static EndpointHit toEntity(EndpointHitDto dto) {

        return EndpointHit.builder()
                .app(dto.getApp())
                .ip(dto.getIp())
                .uri(dto.getUri())
                .timestamp(dto.getTimestamp())
                .build();

    }

    public static EndpointHitDto toDto(EndpointHit entity) {
        return EndpointHitDto.builder()
                .id(entity.getId())
                .app(entity.getApp())
                .ip(entity.getIp())
                .uri(entity.getUri())
                .timestamp(entity.getTimestamp())
                .build();
    }
}