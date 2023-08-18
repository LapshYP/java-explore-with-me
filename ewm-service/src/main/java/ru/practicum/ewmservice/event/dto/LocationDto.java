package ru.practicum.ewmservice.event.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class LocationDto {
    private Float lat;
    private Float lon;
    public LocationDto(Float lat, Float lon) {
    }
}
