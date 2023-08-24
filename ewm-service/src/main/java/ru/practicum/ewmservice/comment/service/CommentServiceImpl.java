package ru.practicum.ewmservice.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.comment.dto.CommentAdminDto;
import ru.practicum.ewmservice.comment.dto.CommentEvent;
import ru.practicum.ewmservice.comment.dto.CommentNew;
import ru.practicum.ewmservice.comment.dto.CommentUserDto;
import ru.practicum.ewmservice.comment.model.Comment;
import ru.practicum.ewmservice.comment.repository.CommentRepoJpa;
import ru.practicum.ewmservice.event.model.Event;
import ru.practicum.ewmservice.event.repository.EventRepoJpa;
import ru.practicum.ewmservice.exception.BadRequestException;
import ru.practicum.ewmservice.exception.NotFoundException;
import ru.practicum.ewmservice.user.model.User;
import ru.practicum.ewmservice.user.repository.UserRepoJpa;

import javax.validation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.ewmservice.event.model.State.PUBLISHED;
import static ru.practicum.ewmservice.event.model.State.PUBLISH_EVENT;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final UserRepoJpa userRepoJpa;
    private final CommentRepoJpa commentRepoJpa;
    private final EventRepoJpa eventRepoJpa;

    private final ModelMapper mapper = new ModelMapper();
    private ModelMapper mapperCast;

    private void validateComment(Comment comment) {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<Comment>> violations = validator.validate(comment);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
        }
    }

    @Override
    @Transactional
    public CommentNew create(Long userId, CommentUserDto inputCommentDto) {

        mapperCast = new ModelMapper();
        TypeMap<Comment, CommentNew> propertyMapper = this.mapperCast.createTypeMap(Comment.class, CommentNew.class);
        propertyMapper.addMappings(
                mapper -> mapper.map(src -> src.getAuthor().getId(), CommentNew::setAuthorId)
        );
        User user = userRepoJpa.findById(userId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь с id = '" + userId + "' не найден"));

        Event event = eventRepoJpa.findById(inputCommentDto.getEventId()).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Событие с id = '" + inputCommentDto.getEventId() + "' не найдено"));
        if (event.getState() != PUBLISHED && event.getState() != PUBLISH_EVENT) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Попытка добавления комментария без публикации");
        }
        Comment comment = mapper.map(inputCommentDto, Comment.class);
        comment.setAuthor(user);
        comment.setCreatedtime(LocalDateTime.now());
        comment.setIsdeleted(false);
        validateComment(comment);
        Comment result = commentRepoJpa.save(comment);
        log.info("create, result.getId() =  {}, commentRepoJpa.size = {}", result.getId(), commentRepoJpa.findAll().size());

        return mapperCast.map(result, CommentNew.class);
    }


    @Override
    public void deleteByUser(Long comId, Long userId) {
        User user = userRepoJpa.findById(userId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь с id = '" + userId + "' не найден"));
        log.info("deleteByUser, user.getId() = {} ", user.getId());
        Comment commentFromDb = commentRepoJpa.findById(comId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Комментарий с id = '" + comId + "' не найден"));
        log.info("deleteByUser, commentFromDb.getId() = {} ", commentFromDb.getId());
        commentFromDb.setIsdeleted(true);
        commentRepoJpa.saveAndFlush(commentFromDb);
        log.info("deleteByUser {}, commentRepoJpa.size = {} ", comId, commentRepoJpa.findAll().size());
    }

    @Override
    public CommentAdminDto deleteByAdmin(Long comId) {
        Comment commentFromDb = commentRepoJpa.findById(comId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Комментарий с id = '" + comId + "' не найден"));
        commentRepoJpa.deleteComment(comId);
        return mapper.map(commentFromDb, CommentAdminDto.class);
    }

    @Override
    @Transactional
    public CommentNew updateComment(Long comId, Long userId, CommentUserDto inputCommentDto) {
        userRepoJpa.findById(userId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь с id = '" + userId + "' не найден"));
        Comment commentFromDb = commentRepoJpa.findById(comId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Комментарий с id = '" + comId + "' не найден"));
        if (commentFromDb.getIsdeleted().equals(true)) {
            throw new BadRequestException(HttpStatus.BAD_REQUEST, "Невозможно обновить удаленный комментарий");
        }
        if (inputCommentDto.getText() == null || inputCommentDto.getText().isBlank()) {
            throw new BadRequestException(HttpStatus.BAD_REQUEST, "Текст комментария не должен быть пустым");
        }

        Comment comment = Comment.builder()
                .id(commentFromDb.getId())
                .text(inputCommentDto.getText())
                .createdtime(commentFromDb.getCreatedtime())
                .event(commentFromDb.getEvent())
                .author(commentFromDb.getAuthor())
                .editedtime(LocalDateTime.now())
                .isdeleted(commentFromDb.getIsdeleted())
                .build();
        validateComment(comment);
        Comment result = commentRepoJpa.save(comment);

        log.info("Выполнено обновление комментария с ID = {}.", comId);
        return mapper.map(result, CommentNew.class);
    }

    @Override
    public List<CommentAdminDto> getAdminAllComments(Long eventId, int from, int size) {

        Pageable pageable = PageRequest.of(
                from == 0 ? 0 : (from / size), size,
                Sort.by(Sort.Direction.ASC, "createdtime")
        );
        List<Comment> comments = commentRepoJpa.getByEvent_Id(eventId, pageable);
        List<CommentAdminDto> result = comments
                .stream()
                .map(comment -> mapper.map(comment, CommentAdminDto.class))
                .collect(Collectors.toList());
        log.info("Выдан список комментариев к событию с ID = {}, состоящий из {} комментариев.",
                eventId, result.size());
        return result;
    }

    @Override
    public List<CommentUserDto> getUserAllComments(Long eventId, int from, int size) {
        Event event = eventRepoJpa.findById(eventId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Событие с id = '" + eventId + "' не найдено"));

        if (event.getState() != PUBLISHED && event.getState() != PUBLISH_EVENT) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Попытка получение информации о комментариях без публикации");
        }
        Pageable pageable = PageRequest.of(
                from == 0 ? 0 : (from / size), size,
                Sort.by(Sort.Direction.ASC, "createdtime")
        );
        List<Comment> comments = commentRepoJpa.getByEvent_Id(eventId, pageable);
        List<CommentUserDto> result = comments
                .stream()
                .filter(comment -> comment.getIsdeleted().equals(false))
                .map(comment -> mapper.map(comment, CommentUserDto.class))
                .collect(Collectors.toList());
        log.info("getUserAllComments,eventId = {}, result.size() = {} ",
                eventId, result.size());
        return result;
    }

    @Override
    public CommentAdminDto getAdminCommentById(Long userId, Long comId) {
        userRepoJpa.findById(userId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь с id = '" + userId + "' не найден"));

        Comment commentFromDb = commentRepoJpa.findById(comId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Комментарий с id = '" + comId + "' не найден"));

        CommentAdminDto result = mapper.map(commentFromDb, CommentAdminDto.class);
        log.info("getCommentById, comId = {}", comId);
        return result;
    }

    @Override
    public CommentUserDto getUserCommentById(Long userId, Long comId) {
        userRepoJpa.findById(userId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь с id = '" + userId + "' не найден"));
        Comment commentFromDb = commentRepoJpa.findById(comId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Комментарий с id = '" + comId + "' не найден"));
        if (commentFromDb.getIsdeleted().equals(true)) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Комментарий с id = '" + comId + "' не найден");
        }
        CommentUserDto result = mapper.map(commentFromDb, CommentUserDto.class);
        log.info("getCommentById, comId = {}", comId);
        return result;
    }

    @Override
    public CommentEvent getAdminCommentsByEventId(Long eventId) {
        return CommentEvent.builder()
                .eventId(eventId)
                .commentCount(commentRepoJpa.getCommentsCountByEventId(eventId))
                .build();
    }
}
