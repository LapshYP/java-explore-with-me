package ru.practicum.statsserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.statsdto.EndpointHitDto;
import ru.practicum.statsdto.ViewStatsDto;
import ru.practicum.statsserver.exception.BadRequestException;
import ru.practicum.statsserver.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {
    private final List<String> stats = new ArrayList<>();
    private final StatsService statsService;

    @PostMapping("/hit")
    public ResponseEntity<EndpointHitDto> saveStats(@Valid @RequestBody EndpointHitDto endpointHitDto) {

        return new ResponseEntity<>(statsService.saveStats(endpointHitDto), HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStatsDto>> getStats(
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime start,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique) {
        ResponseEntity<List<ViewStatsDto>> responseEntity = new ResponseEntity<>(statsService.getStats(start, end, uris, unique), HttpStatus.OK);
        if (responseEntity.getStatusCodeValue() != 200 && !start.isAfter(end)) {
            throw new BadRequestException(HttpStatus.BAD_REQUEST, "Тест на верную обработку запроса без даты начала или без даты конца");
        }
        return responseEntity;
    }
}
