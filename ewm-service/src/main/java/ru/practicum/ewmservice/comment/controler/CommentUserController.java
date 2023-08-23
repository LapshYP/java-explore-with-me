package ru.practicum.ewmservice.comment.controler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.comment.dto.CommentAdminDto;
import ru.practicum.ewmservice.comment.dto.CommentEvent;
import ru.practicum.ewmservice.comment.dto.CommentNew;
import ru.practicum.ewmservice.comment.dto.CommentUserDto;
import ru.practicum.ewmservice.comment.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
public class CommentUserController {
    private final CommentService commentService;

    @PostMapping("/comments/user/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentNew create(@PathVariable("userId") @Positive Long userId,
                             @RequestBody CommentUserDto commentUserDto) {
        log.info("CommentForView POST /comments/user/{userId}, userId = {}, commentUserDto.getEventId() = {}.",
                userId, commentUserDto.getEventId());
        return commentService.create(userId, commentUserDto);

    }


    @DeleteMapping("/comments/{comId}/user/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByUser(@PathVariable @PositiveOrZero Long comId,
                             @PathVariable @PositiveOrZero Long userId) {
        log.info("deleteByUser DELETE /comments/{comId}/user/{userId}, useId = {}, comId = {}.", userId, comId);
        commentService.deleteByUser(comId, userId);
    }

    @DeleteMapping("/admin/comments/{comId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CommentAdminDto deleteByAdmin(@PathVariable @PositiveOrZero Long comId) {
        log.info("deleteByAdmin DELETE /admin/comments/{comId} , comId= {}", comId);
        return commentService.deleteByAdmin(comId);

    }

    @PatchMapping("/comments/{comId}/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentNew updateComment(@PathVariable @PositiveOrZero Long comId,
                                    @PathVariable @PositiveOrZero Long userId,
                                    @RequestBody CommentUserDto updateCommentDto) {
        log.info("updateComment PATCH /comments/{comId}/user/{userId}, comId = {}, userId = {}.", comId, userId);
        return commentService.updateComment(comId, userId, updateCommentDto);
    }

    @GetMapping("/admin/comments/event/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentAdminDto> getAdminAllComments(@PathVariable @PositiveOrZero Long eventId,
                                                     @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                     @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("GET /admin/comments/event/{eventId}, eventId = {}, from={} , size={}", eventId, eventId, from, size);
        return commentService.getAdminAllComments(eventId, from, size);
    }

    @GetMapping("/comments/event/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentUserDto> getUserAllComments(@PathVariable @PositiveOrZero Long eventId,
                                                   @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                   @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("GET /comments/event/{eventId}, eventId = {}, from={} , size={}", eventId, eventId, from, size);
        return commentService.getUserAllComments(eventId, from, size);
    }


    @GetMapping("/comments/{comId}/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentUserDto getUserCommentById(@PathVariable("userId") @Positive Long userId,
                                             @PathVariable("comId") @Positive Long comId) {
        log.info("getByIdForUser ПУ {} пользователем с ID = {}.", comId, userId);
        return commentService.getUserCommentById(userId, comId);
    }

    @GetMapping("/admin/comments/{comId}/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentAdminDto getAdminCommentById(@PathVariable("userId") @Positive Long userId,
                                               @PathVariable("comId") @Positive Long comId) {
        log.info("getByIdForUser ПУ {} пользователем с ID = {}.", comId, userId);
        return commentService.getAdminCommentById(userId, comId);
    }

    @GetMapping("/admin/comments/events/{eventId}")
    public ResponseEntity<CommentEvent> getAdminCommentsByEventId(@PathVariable Long eventId) {
        log.info("getById GET /events/{id} , id = {}, servletRequest = {} ", eventId);
        return new ResponseEntity<>(commentService.getAdminCommentsByEventId(eventId), HttpStatus.OK);
    }
}
