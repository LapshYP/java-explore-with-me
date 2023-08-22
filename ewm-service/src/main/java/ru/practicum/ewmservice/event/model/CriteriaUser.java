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
public class CriteriaUser {

  String text;
  List<Long> categories;
  Boolean paid;
  LocalDateTime rangeStart;
  LocalDateTime rangeEnd;
  Boolean onlyAvailable;

}
