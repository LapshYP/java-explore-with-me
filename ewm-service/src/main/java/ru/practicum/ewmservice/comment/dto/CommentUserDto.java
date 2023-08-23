package ru.practicum.ewmservice.comment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentUserDto {
    Long id;
    Long eventId;
    String text;
    LocalDateTime createdtime;
}
