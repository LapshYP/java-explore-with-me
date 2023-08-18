package ru.practicum.ewmservice.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.request.dto.RequestDto;
import ru.practicum.ewmservice.request.service.RequestEventService;

import java.util.List;


@RestController
@Slf4j
@RequiredArgsConstructor
public class EventRequestController {
    private final RequestEventService requestItemService;

    @PostMapping("/users/{userId}/requests")
    ResponseEntity<RequestDto> create(@RequestParam(name = "eventId") Long eventId,
                                      @PathVariable("userId") Long userId) {
        log.info("create POST /users/{userId}/requests, eventId = {}, userId = {}",  eventId,userId);
        return new ResponseEntity<>(requestItemService.create(eventId, userId), HttpStatus.CREATED);
    }

    @GetMapping
    List<RequestDto> requestsGet(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("EventRequestController GET , userId={} ", userId);

        return requestItemService.requestsGet(userId);

    }

    @GetMapping(path = "/all")
    List<RequestDto> requestsAllGet(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestParam(name = "from", defaultValue = "0") Integer from,
                                    @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("requestsAllGet GET /all, userId={}, from={}, size={}", userId, from, size);

        return requestItemService.requestsAllGet(userId, from, size);
    }

    @GetMapping("{requestId}")
    public RequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long requestId) {
        log.info("getRequestById GET {requestId}, userId={}, requestId={} ", userId, requestId);

        return requestItemService.getRequestById(userId, requestId);
    }
}
