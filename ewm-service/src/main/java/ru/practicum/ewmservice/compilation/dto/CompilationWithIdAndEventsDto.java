package ru.practicum.ewmservice.compilation.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewmservice.event.dto.EventDto;
import ru.practicum.ewmservice.event.dto.EventShortDto;


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
    Long id;
    String title;
    Boolean pinned;
     Set<EventShortDto> events;
}
