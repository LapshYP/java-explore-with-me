package ru.practicum.ewmservice.event.model;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PACKAGE)
public class CriteriaAdmin {

    List<Long> users;
    List<State> states;
    List<Long> categories;
    LocalDateTime rangeStart;
    LocalDateTime rangeEnd;

}
