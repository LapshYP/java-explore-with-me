package ru.practicum.ewmservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
public class NotFoundException extends ResponseStatusException {
    public NotFoundException(HttpStatus status, String msg) {
        super(status, msg);
        log.error(msg);
    }

}
