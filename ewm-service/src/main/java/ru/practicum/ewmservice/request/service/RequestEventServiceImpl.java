package ru.practicum.ewmservice.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.event.model.Event;
import ru.practicum.ewmservice.event.repository.EventRepoJpa;
import ru.practicum.ewmservice.exception.ConflictException;
import ru.practicum.ewmservice.exception.NotFoundException;
import ru.practicum.ewmservice.request.dto.RequestDto;
import ru.practicum.ewmservice.request.model.Request;
import ru.practicum.ewmservice.request.model.Status;
import ru.practicum.ewmservice.request.repossitory.RequestEventRepoJpa;
import ru.practicum.ewmservice.user.model.User;
import ru.practicum.ewmservice.user.repository.UserRepoJpa;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewmservice.event.model.State.PENDING;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestEventServiceImpl implements RequestEventService {

    private final RequestEventRepoJpa requestEventRepoJpa;
    private final EventRepoJpa eventRepoJpa;
    private final UserRepoJpa userRepoJpa;

    @Override
    @Transactional
    public RequestDto create(Long eventId, Long userId) {

        User requester = userRepoJpa.findById(userId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Юзер с таким именем не найден в базе данных"));
        Event event = eventRepoJpa.findById(eventId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Событие с таким именем не найден в базе данных"));

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Добавление запроса от инициатора мероприятия на участие в нем");
        }
        if (event.getState() == PENDING) {
            throw new ConflictException("Добавление запроса на участие в неопубликованном мероприятии");
        }
        Request request = new Request();
        request.setCreated(LocalDateTime.now());
        request.setRequester(requester);
        Long participantLimit = event.getParticipantLimit();
        if (participantLimit == 0) {
            request.setStatus(Status.CONFIRMED);
        } else {

            Long count = requestEventRepoJpa.countParticipantLimit(eventId);
            if (event.getParticipantLimit().equals(count)) {
                throw new ConflictException("Добавление запроса на участие в событии, у которого заполнен лимит участников");
            }
            request.setStatus(Status.PENDING);
        }


        request.setDescription("event");
        log.debug("Запрос на участие создан, eventId = {},userId = {}   ", eventId, userId);
        request.setEvent(event);
        Request saved = requestEventRepoJpa.save(request);
        return RequestDto.builder()
                .id(saved.getId())
                .created(saved.getCreated())
                .event(saved.getEvent().getId())
                .requester(saved.getRequester().getId())
                .status(saved.getStatus())
                .build();
    }

    @Override
    public List<RequestDto> requestsGet(Long userId) {
        return null;
    }

    @Override
    public List<RequestDto> requestsAllGet(Long userId, int from, int size) {
        return null;
    }

    @Override
    public RequestDto getRequestById(Long userId, Long requestId) {
        return null;
    }

}
