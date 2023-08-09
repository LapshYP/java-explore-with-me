package ru.practicum.ewmservice.event.mapper;


import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.modelmapper.ModelMapper;
import ru.practicum.ewmservice.category.model.Category;
import ru.practicum.ewmservice.event.dto.EventDto;
import ru.practicum.ewmservice.event.model.Event;
import ru.practicum.ewmservice.event.model.Location;
import ru.practicum.ewmservice.event.model.State;

import java.time.LocalDateTime;


@UtilityClass

public class EventMapper {
private final   ModelMapper mapper = new ModelMapper();
    public static Event toEntity(EventDto dto) {

        return Event.builder()
                .annotation(dto.getAnnotation())
                .createdOn(LocalDateTime.now())
                .description(dto.getDescription())
                .date(dto.getEventDate())
                .location(new Location(dto.getLocation().getLat(), dto.getLocation().getLon()))
                .paid(dto.isPaid())
                .participantLimit(dto.getParticipantLimit())
                .requestModeration(dto.isRequestModeration())
                .state(State.PENDING)
                .title(dto.getTitle())
                .requestModeration(true)
                .publishedOn(LocalDateTime.now())
                .request(dto.getRequestId())
//                    .category(new Category(15L,"without category"))
                .build();
//    public static EndpointHitDto toDto(EndpointHit entity) {
//        return EndpointHitDto.builder()
//                .id(entity.getId())
//                .app(entity.getApp())
//                .ip(entity.getIp())
//                .uri(entity.getUri())
//                .timestamp(entity.getTimestamp())
//                .build();
    }
}