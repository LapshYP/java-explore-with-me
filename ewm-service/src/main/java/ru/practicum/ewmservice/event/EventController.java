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

//  {"annotation":"Numquam officia doloribus enim deserunt corporis aut sed. Facere est voluptatem quis sed non autem veniam voluptatem quaerat. Tempora voluptate vel molestiae reprehenderit aperiam provident praesentium maiores. Deleniti sapiente voluptas et praesentium aliquid et. Voluptatibus consequatur sapiente enim rerum. Quasi corrupti qui dolor nisi qui corrupti dolore impedit.","category":25,"description":"Saepe nihil aut consequuntur. Est nulla asperiores adipisci rerum consequuntur a maxime explicabo quis. Facilis cupiditate neque. Atque sequi neque corrupti voluptatem sunt non. Non ut et sint dolorem. Libero est magnam ipsum voluptas nihil voluptatum deserunt.\n \rNecessitatibus accusantium aliquam aliquid blanditiis id quia ipsum vel. In et aut et expedita praesentium accusantium velit. Deleniti sed sunt mollitia dolor cupiditate officiis voluptatem excepturi. Quisquam voluptatibus assumenda aliquam repellat numquam assumenda quae. Voluptatibus cumque dolor asperiores corporis eum officiis. Recusandae inventore quia sed quas consequatur cupiditate.\n \rVoluptatem nemo rerum ea sed illum enim veniam eaque aut. Provident voluptatem officia laboriosam provident vero ratione facilis est. Qui voluptates fugit magni neque voluptatibus illum facilis iusto provident. Aut debitis nemo sequi similique nobis asperiores.",
//  "eventDate":"2023-08-11 03:54:18","location":{"lat":80.4441,"lon":-171.9367},"paid":"true","participantLimit":"75","requestModeration":"false","title":"Non dolores consequatur repudiandae suscipit consequuntur deserunt."}
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

    //    @GetMapping("/admin/events")
//    public ResponseEntity<List<EventDto>> getAll(@RequestParam(required = false) String users,
//                                                 @RequestParam(required = false) String states,
//                                                 @RequestParam(required = false) String categories,
//                                                 @RequestParam(required = false) String rangeStart,
//                                                 @RequestParam(required = false) String rangeEnd,
//                                                 @RequestParam(defaultValue = "0") @PositiveOrZero int from,
//                                                 @RequestParam(defaultValue = "10") @Positive int size) {
//        log.info("Получен запрос GET /admin/events   users = {}",users);
//
//       return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
//    }
//
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

//    @PatchMapping("/users/{userId}/events/{eventId}/requests")
//    public ResponseEntity< EventRequestStatusUpdateResult > updateByUserIdAndEventIdRequests( @PathVariable Long userId, @PathVariable Long eventId) {
//        return new ResponseEntity<>(eventService.updateByUserIdAndEventIdRequests( userId,eventId), HttpStatus.OK);
//    }

    @GetMapping("/users/{userId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getByUserIdRequests(@PathVariable Long userId) {
        log.info("getByUserIdRequests GET /users/{userId}/requests ,  userId = {} ", userId);

        return new ResponseEntity<>(eventService.getByUserIdRequests(userId), HttpStatus.OK);
    }

//    @GetMapping("/events")
//    public List<EventDto> searchFilms(@RequestParam(required = false) String text,
//                                      @RequestParam(required = false) List<Long> categories,
//                                      @RequestParam(required = false) Boolean paid,
//                                      @RequestParam(required = false)
//                                      //@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime rangeStart,
//                                      String rangeStart,
//                                      @RequestParam(required = false)
//                                      String rangeEnd,
//                                      @RequestParam(required = false) Boolean onlyAvailable,
//                                      @RequestParam(required = false) String sort,
//                                      @RequestParam(name = "from", defaultValue = "0") int from,
//                                      @RequestParam(name = "size", defaultValue = "10") int size) {
//        log.info("searchByParam GET /events , text = {}, categories = {}, eventId = {} ", text, categories, paid);
//
//        return eventService.searchByParam(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
//    }

    @PatchMapping("/admin/events/{eventId}")
    public ResponseEntity<EventDto> updateByAdmin(@RequestBody UpdateEventAdminRequest updateEventAdminRequest, @PathVariable Long eventId) {
        log.info("updateByAdmin PATCH /admin/events/{eventId} , updateEventAdminRequest = {},  eventId = {} ", updateEventAdminRequest, eventId);
        //        {"annotation":"Dolorum voluptates velit natus cumque maiores qui commodi quos. Et nobis ea et est neque natus impedit ut vitae. Officia esse adipisci sed fugit.",
//        "category":89,
//        "description":"Ea sint quaerat minus quod consequatur aut reiciendis. Aut placeat atque aliquid omnis. Animi nobis nihil quisquam atque repellat ut repellat voluptates beatae.\n \rHic quis ratione corporis. Atque ipsa ut repudiandae dicta sit eligendi laboriosam aut consequatur. Dolor perferendis hic corporis dignissimos similique possimus. Ex hic asperiores voluptatem atque consequatur aliquid odit enim. Odio consequatur fuga quis blanditiis nisi dignissimos quod reprehenderit.\n \rVoluptatum tempora perspiciatis tempore voluptas autem amet. Sed ab et dolor. Soluta doloribus ut eos reprehenderit praesentium.",
//        "eventDate":"2023-08-12 22:47:19",
//        "location":{"lat":29.1028,"lon":125.1484},
//        "paid":"true",
//        "participantLimit":"938",
//        "requestModeration":"false",
//        "title":"Sit id perspiciatis voluptatem et qui animi.",
//        "stateAction":"PUBLISH_EVENT"

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
//    @PatchMapping("admin/events/{eventId}")
//    public ResponseEntity<UpdateEventAdminRequest> update(@RequestBody EventDto eventDto, @PathVariable Long eventId, @RequestParam  Long userId) {
//        return new ResponseEntity<>(eventService.update(eventDto, eventId, userId), HttpStatus.OK);
//    }

//    @GetMapping
//    public ResponseEntity<List<EventDto>> getItemByUserId(@RequestHeader("X-Sharer-User-Id") int userId,
//                                                          @RequestParam(name = "from", defaultValue = "0") int from,
//                                                          @RequestParam(name = "size", defaultValue = "20") int size) {
//        return new ResponseEntity<>(eventService.getByBookerIdService(userId,from,size), HttpStatus.OK);
//    }
//
//    @GetMapping("/{itemId}")
//    public ResponseEntity<EventDto> getItemById(@Valid @PathVariable int itemId, @RequestHeader("X-Sharer-User-Id") int userId) {
//        return new ResponseEntity<>(eventService.getByOwnerIdService(itemId, userId), HttpStatus.OK);
//    }

//

//
//
//
//
//
//

//
//    @PostMapping("/{itemId}/comment")
//    public CommentDto addCommentToItem(@RequestHeader("X-Sharer-User-Id") int userId,
//                                       @PathVariable int itemId, @RequestBody CommentDto commentDto) {
//        return eventService.addComment(userId, itemId, commentDto);
//    }
}
