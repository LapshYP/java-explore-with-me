package ru.practicum.ewmservice.event.mapper;


import lombok.experimental.UtilityClass;
import org.modelmapper.ModelMapper;
import ru.practicum.ewmservice.category.dto.CategoryDto;
import ru.practicum.ewmservice.event.dto.EventDto;
import ru.practicum.ewmservice.event.dto.EventNewDto;
import ru.practicum.ewmservice.event.dto.LocationDto;
import ru.practicum.ewmservice.event.model.Event;
import ru.practicum.ewmservice.event.model.Location;
import ru.practicum.ewmservice.event.model.State;
import ru.practicum.ewmservice.user.dto.UserDto;

import java.time.LocalDateTime;


@UtilityClass

public class EventMapper {
    private final ModelMapper mapper = new ModelMapper();

    public static Event toEntity(EventNewDto dto) {

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
                .build();
    }

    public static EventDto toEventDto(Event entity) {
        Float lat = entity.getLocation().getLat();
        Float lon = entity.getLocation().getLon();
        return EventDto.builder()
                .id(entity.getId())
                .annotation(entity.getAnnotation())
                .category(mapper.map(entity.getCategory(), CategoryDto.class))
                .confirmedRequests(entity.getConfirmedRequests())
                .createdOn(entity.getCreatedOn())
                .description(entity.getDescription())
                .eventDate(entity.getDate())
                .initiator(mapper.map(entity.getInitiator(), UserDto.class))
                .location(mapper.map(entity.getLocation(), LocationDto.class))
                .paid(entity.getPaid())
                .participantLimit(entity.getParticipantLimit())
                .publishedOn(entity.getPublishedOn())
                .requestModeration(entity.getRequestModeration())
                .state(entity.getState())
                .title(entity.getTitle())
                .views(entity.getViews())
                .build();
    }
}