package ru.practicum.ewmservice.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewmservice.comment.validation.Create;
import ru.practicum.ewmservice.comment.validation.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentUserDto {

    @PositiveOrZero(groups = Update.class)
    Long id;
    @PositiveOrZero(groups = Create.class)
    @PositiveOrZero(groups = Update.class)
    Long eventId;
    @NotBlank(groups = Create.class)
    @NotBlank(groups = Update.class)
    @Size(min = 1, max = 2000, groups = Create.class)
    @Size(min = 1, max = 2000, groups = Update.class)
    String text;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdtime;
}
