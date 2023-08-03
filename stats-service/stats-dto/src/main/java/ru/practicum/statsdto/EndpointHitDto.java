package ru.practicum.statsdto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;


@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class EndpointHitDto {
    Long id;
    @NotBlank(message = "must not be blank")
    String app;
    @NotBlank(message = "must not be blank")
    String uri;
    @NotBlank(message = "must not be blank")
    String ip;
    @NotNull(message = "must not be null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime timestamp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EndpointHitDto)) return false;
        EndpointHitDto hitDto = (EndpointHitDto) o;
        return Objects.equals(getApp(), hitDto.getApp()) && Objects.equals(getUri(), hitDto.getUri()) && Objects.equals(getIp(), hitDto.getIp()) && Objects.equals(getTimestamp(), hitDto.getTimestamp());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getApp(), getUri(), getIp(), getTimestamp());
    }
}
