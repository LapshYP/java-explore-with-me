package ru.practicum.ewmservice.exception;

public class UnsupportedStatusException extends RuntimeException {
    public UnsupportedStatusException(String msg) {
        super(msg);
    }
}
