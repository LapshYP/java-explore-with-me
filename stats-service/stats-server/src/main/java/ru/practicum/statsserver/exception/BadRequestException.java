package ru.practicum.statsserver.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

// sets the HTTP status code to 400
@Slf4j
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends ResponseStatusException {
    public BadRequestException(HttpStatus httpStatus, String msg) {
        super(httpStatus, msg);
        log.error(msg);
    }

}