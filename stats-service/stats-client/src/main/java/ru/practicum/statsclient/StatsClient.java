package ru.practicum.statsclient;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.statsdto.EndpointHitDto;

import java.util.List;

@Service
public interface StatsClient {
    ResponseEntity<Object> saveStats(EndpointHitDto endpointHit);

    ResponseEntity<Object> getStats(
            String start, String end, List<String> uris, Boolean unique);


}
