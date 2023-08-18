package ru.practicum.ewmservice.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewmservice.event.dto.EventDto;
import ru.practicum.ewmservice.event.model.State;
import ru.practicum.ewmservice.request.model.Status;
import ru.practicum.ewmservice.user.dto.UserDto;


import java.time.LocalDateTime;


@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class RequestDto {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime created;
    Long event;
    Long id;
    Long requester;
//    String description;






    Status status;
}
