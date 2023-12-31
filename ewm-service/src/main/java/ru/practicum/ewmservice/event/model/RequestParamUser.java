package ru.practicum.ewmservice.event.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestParamUser {

    String text;
    List<Long> categories;
    Boolean paid;
    LocalDateTime rangeStart;
    LocalDateTime rangeEnd;
    Boolean onlyAvailable;
    String sort;
    int from;
    int size;
    HttpServletRequest request;
}