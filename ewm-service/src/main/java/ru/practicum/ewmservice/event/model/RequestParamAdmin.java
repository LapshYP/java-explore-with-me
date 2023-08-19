package ru.practicum.ewmservice.event.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PACKAGE)
public class RequestParamAdmin {

    List<Long> users;
    List<State> states;
    List<Long> categories;
    LocalDateTime rangeStart;
    LocalDateTime rangeEnd;
    int from;
    int size;
}