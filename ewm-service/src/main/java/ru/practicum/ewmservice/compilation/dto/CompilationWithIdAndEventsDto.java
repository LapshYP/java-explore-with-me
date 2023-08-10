package ru.practicum.ewmservice.compilation.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewmservice.event.dto.EventDto;


import java.util.Set;

@Data
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CompilationWithIdAndEventsDto {
    int id;
    String title;
    Boolean pinned;
     Set<EventDto> events;
}
