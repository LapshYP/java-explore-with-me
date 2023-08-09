package ru.practicum.ewmservice.category.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

@Data
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CategoryDto {
//    int id;
    String name;

}
