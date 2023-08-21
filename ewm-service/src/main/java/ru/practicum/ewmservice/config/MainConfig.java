package ru.practicum.ewmservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.practicum.statsclient.StatsClientImpl;


@Configuration
public class MainConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public StatsClientImpl statsClient() {
        return new StatsClientImpl(new RestTemplate());
    }
}
