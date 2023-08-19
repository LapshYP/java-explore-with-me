package ru.practicum.ewmservice.stats;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.practicum.statsclient.StatsClient;


@Configuration
public class MainConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public StatsClient statsClient() {
        return new StatsClient(new RestTemplate());
    }
}
