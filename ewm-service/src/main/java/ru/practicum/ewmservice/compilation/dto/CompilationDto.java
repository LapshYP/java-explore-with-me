package ru.practicum.ewmservice.compilation.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CompilationDto {
//    int id;
    String title;
    Boolean pinned;
     Set<Long> events;
}
