package ru.practicum.ewmservice.comment.service;


import org.springframework.stereotype.Service;
import ru.practicum.ewmservice.comment.dto.CommentAdminDto;
import ru.practicum.ewmservice.comment.dto.CommentEvent;
import ru.practicum.ewmservice.comment.dto.CommentNew;
import ru.practicum.ewmservice.comment.dto.CommentUserDto;

import java.util.List;

@Service
public interface CommentService {

    CommentNew create(Long userId, CommentUserDto inputCommentDto);

    CommentUserDto getUserCommentById(Long userId, Long comId);

    void deleteByUser(Long comId, Long userId);


    CommentAdminDto deleteByAdmin(Long comId);

    CommentNew updateComment(Long comId, Long userId, CommentUserDto inputCommentDto);

    List<CommentAdminDto> getAdminAllComments(Long eventId, int from, int size);

    List<CommentUserDto> getUserAllComments(Long eventId, int from, int size);

    CommentAdminDto getAdminCommentById(Long userId, Long comId);

    CommentEvent getAdminCommentsByEventId(Long eventId);

}
