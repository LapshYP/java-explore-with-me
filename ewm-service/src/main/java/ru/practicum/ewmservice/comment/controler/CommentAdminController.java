package ru.practicum.ewmservice.comment.controler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.comment.dto.CommentAdminDto;
import ru.practicum.ewmservice.comment.dto.CommentEvent;
import ru.practicum.ewmservice.comment.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
public class CommentAdminController {
    private final CommentService commentService;


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

    @DeleteMapping("/admin/comments/{comId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CommentAdminDto deleteByAdmin(@PathVariable @PositiveOrZero Long comId) {
        log.info("deleteByAdmin DELETE /admin/comments/{comId} , comId= {}", comId);
        return commentService.deleteByAdmin(comId);

    }

    @GetMapping("/admin/comments/event/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentAdminDto> getAdminAllComments(@PathVariable @PositiveOrZero Long eventId,
                                                     @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                     @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("GET /admin/comments/event/{eventId}, eventId = {}, from={} , size={}", eventId, eventId, from, size);
        return commentService.getAdminAllComments(eventId, from, size);
    }
}
