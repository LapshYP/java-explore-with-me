package ru.practicum.ewmservice.event.model;

import lombok.*;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Location {
    private Float lat;
    private Float lon;
}
