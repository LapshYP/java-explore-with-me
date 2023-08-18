package ru.practicum.ewmservice.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewmservice.category.dto.CategoryDto;
import ru.practicum.ewmservice.event.model.State;
import ru.practicum.ewmservice.user.dto.UserDto;

import java.time.LocalDateTime;


@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class EventDto {
    Long id;
    String annotation;
    CategoryDto category;
    long confirmedRequests;
    LocalDateTime createdOn;
    String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    UserDto initiator;
    LocationDto location;
    boolean paid;
    long participantLimit;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime publishedOn;
    boolean requestModeration;
    State state;
    String title;
    Long views;

}
