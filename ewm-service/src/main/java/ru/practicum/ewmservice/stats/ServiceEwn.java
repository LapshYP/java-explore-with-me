package ru.practicum.ewmservice.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.statsclient.StatsClient;
import ru.practicum.statsdto.EndpointHitDto;

import java.time.LocalDateTime;
import java.util.ArrayList;


@Service
@RequiredArgsConstructor
public class ServiceEwn {
    private final StatsClient client = new StatsClient(new RestTemplate());

    public int getSmth() {

        return client.getStats("2020-05-05 00:00:00", "2035-05-05 00:00:00", new ArrayList<>(), true).getStatusCodeValue();
    }

    public int postSmth() {
        EndpointHitDto endpointHit = EndpointHitDto.builder()
                .id(1L)
                .app("wm-main-service")
                .uri("/events/1")
                .ip("192.163.0.1")
                .timestamp(LocalDateTime.now())
                .build();
        return client.saveStats(endpointHit).getStatusCodeValue();
    }
}
