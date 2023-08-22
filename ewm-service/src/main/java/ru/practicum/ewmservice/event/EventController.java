package ru.practicum.ewmservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.event.dto.*;
import ru.practicum.ewmservice.event.model.RequestParamAdmin;
import ru.practicum.ewmservice.event.model.RequestParamUser;
import ru.practicum.ewmservice.event.model.State;
import ru.practicum.ewmservice.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping("/users/{userId}/events")
    public ResponseEntity<EventDto> create(
            @RequestBody EventNewDto eventNewDto, @PathVariable Long userId) {

        log.info("create POST /users/{userId}/events , userId = {}", userId);
        return new ResponseEntity<>(eventService.create(eventNewDto, userId),
                HttpStatus.CREATED);
    }

    @GetMapping("/events")
    public ResponseEntity<Set<EventShortDto>> getAllPublic(@RequestParam(required = false) String text,
                                                           @RequestParam(required = false) List<Long> categories,
                                                           @RequestParam(required = false) Boolean paid,
                                                           @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                           @RequestParam(required = false) LocalDateTime rangeStart,
                                                           @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                           @RequestParam(required = false) LocalDateTime rangeEnd,
                                                           @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                           @RequestParam(defaultValue = "EVENT_DATE") String sort,
                                                           @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                           @RequestParam(defaultValue = "10") @Positive int size,
                                                           HttpServletRequest request) {
        log.info("getAllPublic GET /events,  text = {}, categories = {}, paid = {}, rangeStart = {}, " +
                        "rangeEnd = {}, onlyAvailable = {}, sort = {}, from = {}, size = {}", text, categories, paid,
                rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        RequestParamUser param = RequestParamUser.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable)
                .sort(sort)
                .from(from)
                .size(size)
                .request(request)
                .build();
        return new ResponseEntity<>(eventService.getAllPublic(param), HttpStatus.OK);
    }

    @GetMapping("/admin/events")
    public ResponseEntity<List<EventDto>> getAllAdmin(@RequestParam(required = false) List<Long> users,
                                                      @RequestParam(required = false) List<String> states,
                                                      @RequestParam(required = false) List<Long> categories,
                                                      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                      @RequestParam(required = false) LocalDateTime rangeStart,
                                                      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                      @RequestParam(required = false) LocalDateTime rangeEnd,
                                                      @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                      @RequestParam(defaultValue = "10") @Positive int size) {


        List<State> statesEnum = null;
        if (states != null) {
            statesEnum = states.stream()
                    .map(State::from)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        RequestParamAdmin param = RequestParamAdmin.builder()
                .users(users)
                .states(statesEnum)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build();
        log.info("getAllAdmin, GET /admin/events, param = {}", param);
        return new ResponseEntity<>(eventService.getAllAdmin(param), HttpStatus.OK);
    }


    @GetMapping("/users/{userId}/events")
    public ResponseEntity<List<EventDto>> getByUserId(@PathVariable Long userId,
                                                      @RequestParam(name = "from", defaultValue = "0") int from,
                                                      @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("getByUserId GET /users/{userId}/events , userId = {}, from = {}, size = {}", userId, from, size);
        return new ResponseEntity<>(eventService.getByUserId(userId, from, size), HttpStatus.OK);
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<EventDto> getById(@PathVariable Long id, HttpServletRequest servletRequest) {
        log.info("getById GET /events/{id} , id = {}, servletRequest = {} ", id, servletRequest);
        return new ResponseEntity<>(eventService.getById(id, servletRequest), HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public ResponseEntity<EventDto> getByUserIdAndEventId(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("getByUserIdAndEventId GET /users/{userId}/events/{eventId} , userId = {}, eventId = {} ", userId, eventId);
        return new ResponseEntity<>(eventService.getByUserIdAndEventId(userId, eventId), HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getByUserIdAndEventIdRequests(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("getRequests GET /users/{userId}/events/{eventId}/requests , userId = {}, eventId = {} ", userId, eventId);
        return new ResponseEntity<>(eventService.getRequests(userId, eventId), HttpStatus.OK);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> updateRequestStatus(@RequestBody(required = false) EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest, @PathVariable Long userId, @PathVariable Long eventId) {
        log.info("updateRequestStatus PATCH /users/{userId}/events/{eventId}/requests , eventRequestStatusUpdateRequest = {}, userId = {}, eventId = {} ", eventRequestStatusUpdateRequest, userId, eventId);
        return new ResponseEntity<>(eventService.updateRequestStatus(eventRequestStatusUpdateRequest, userId, eventId), HttpStatus.OK);
    }


    @GetMapping("/users/{userId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getByUserIdRequests(@PathVariable Long userId) {
        log.info("getByUserIdRequests GET /users/{userId}/requests ,  userId = {} ", userId);
        return new ResponseEntity<>(eventService.getByUserIdRequests(userId), HttpStatus.OK);
    }


    @PatchMapping("/admin/events/{eventId}")
    public ResponseEntity<EventDto> updateByAdmin(@RequestBody UpdateEventAdminRequest updateEventAdminRequest, @PathVariable Long eventId) {
        log.info("updateByAdmin PATCH /admin/events/{eventId} , updateEventAdminRequest = {},  eventId = {} ", updateEventAdminRequest, eventId);
        return new ResponseEntity<>(eventService.updateByAdmin(updateEventAdminRequest, eventId), HttpStatus.OK);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public ResponseEntity<EventDto> updateByUser(@RequestBody UpdateEventUserRequest updateEventUserRequest, @PathVariable Long eventId, @PathVariable Long userId) {
        log.info("updateByUser PATCH /users/{userId}/events/{eventId} , updateEventUserRequest = {},  eventId = {},  userId = {}  ", updateEventUserRequest, eventId, userId);
        return new ResponseEntity<>(eventService.updateByUser(updateEventUserRequest, eventId, userId), HttpStatus.OK);
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> updateByUserCancel(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("updateByUserCancel PATCH /users/{userId}/requests/{requestId}/cancel ,  userId = {},  requestId = {}  ", userId, requestId);
        return new ResponseEntity<>(eventService.updateByUserCancel(userId, requestId), HttpStatus.OK);
    }
}
