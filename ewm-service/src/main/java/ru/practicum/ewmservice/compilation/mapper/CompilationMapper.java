package ru.practicum.ewmservice.compilation.mapper;


import lombok.experimental.UtilityClass;
import org.modelmapper.ModelMapper;
import ru.practicum.ewmservice.compilation.dto.CompilationWithEventsDto;
import ru.practicum.ewmservice.compilation.model.Compilation;


@UtilityClass

public class CompilationMapper {
    private final ModelMapper mapper = new ModelMapper();

    //    public static Event toEntity(EventDto dto) {
//
//        return Event.builder()
//                .annotation(dto.getAnnotation())
//                .createdOn(LocalDateTime.now())
//                .description(dto.getDescription())
//                .date(dto.getEventDate())
//                .location(new Location(dto.getLocation().getLat(), dto.getLocation().getLon()))
//                .paid(dto.isPaid())
//                .participantLimit(dto.getParticipantLimit())
//                .requestModeration(dto.isRequestModeration())
//                .state(State.PENDING)
//                .title(dto.getTitle())
//                .requestModeration(true)
//                .publishedOn(LocalDateTime.now())
//                .request(dto.getRequestId())
//                    .category(new Category(15L,"without category"))
    //      .build();
//    public static CompilationWithEventsDto toDto(Compilation entity) {
//        return CompilationWithEventsDto.builder()
//                .events(entity.getEvents())
//                .pinned(entity.getPinned())
//                .title(entity.getTitle())
//                .build();
//    }
}