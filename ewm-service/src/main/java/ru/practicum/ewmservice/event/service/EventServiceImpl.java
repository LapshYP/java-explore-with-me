package ru.practicum.ewmservice.event.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.category.model.Category;
import ru.practicum.ewmservice.category.repository.CategoryRepoJpa;
import ru.practicum.ewmservice.event.dto.*;
import ru.practicum.ewmservice.event.mapper.EventMapper;
import ru.practicum.ewmservice.event.model.*;
import ru.practicum.ewmservice.event.repository.EventRepoJpa;
import ru.practicum.ewmservice.exception.BadRequestException;
import ru.practicum.ewmservice.exception.ConflictException;
import ru.practicum.ewmservice.exception.NotFoundException;
import ru.practicum.ewmservice.request.model.Request;
import ru.practicum.ewmservice.request.model.Status;
import ru.practicum.ewmservice.request.repossitory.RequestEventRepoJpa;
import ru.practicum.ewmservice.stats.ServiceEwn;
import ru.practicum.ewmservice.user.model.User;
import ru.practicum.ewmservice.user.repository.UserRepoJpa;
import ru.practicum.statsclient.StatsClient;

import javax.servlet.http.HttpServletRequest;
import javax.validation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.ewmservice.event.model.State.*;
import static ru.practicum.ewmservice.request.model.Status.CONFIRMED;
import static ru.practicum.ewmservice.request.model.Status.REJECTED;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepoJpa eventRepoJpa;
    private final UserRepoJpa userRepoJpa;
    private final CategoryRepoJpa categoryRepoJpa;
    private final RequestEventRepoJpa requestEventRepoJpa;
    private final ServiceEwn serviceEwn;

    private final ModelMapper mapper = new ModelMapper();
    private final HashSet<String> requestHashSet = new HashSet<>();
    private final HashMap<Long, HashSet<String>> httpServletRequests = new HashMap<Long, HashSet<String>>();


    private final StatsClient statsClient;

    @Value("${app.stats.url}")
    String serviceName;


    private void validateItem(Event event) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Event>> violations = validator.validate(event);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);

        }
    }

    @SneakyThrows
    @Transactional
    public EventDto create(EventNewDto eventNewDto, Long userId) {
        User user = userRepoJpa.findById(userId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь с id = '" + userId + "' не найден"));
        Category category = categoryRepoJpa.findById(eventNewDto.getCategory()).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Категория с id = '" + userId + "' не найдена"));
        Event event = EventMapper.toEntity(eventNewDto);
        if (event.getDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException(HttpStatus.BAD_REQUEST, "дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента");
        }

        event.setPublishedOn(LocalDateTime.now());
        event.setInitiator(user);
        event.setCategory(category);
        event.setViews(0L);


        validateItem(event);
        Event createdEvent = eventRepoJpa.save(event);
        log.debug("Событие создано, id = {}, confirmedRequests = {}", event.getId(), event.getConfirmedRequests());

        return mapper.map(createdEvent, EventDto.class);
    }


    @Override
    public List<EventDto> getByUserId(Long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        log.debug("getByUserId, from = {}, size = {}", from, size);
        return eventRepoJpa.findByUserId(userId, pageable).stream().map(event -> mapper.map(event, EventDto.class)).collect(Collectors.toList());
    }

    @Override
    public EventDto getById(Long eventId, HttpServletRequest servletRequest) {
        requestHashSet.add(servletRequest.getRemoteAddr());
        httpServletRequests.put(eventId, requestHashSet);

        Event event = eventRepoJpa.findById(eventId).get();
        if (event.getState() != PUBLISHED && event.getState() != PUBLISH_EVENT) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Попытка получения информации о событии по публичному эндпоинту без публикации");
        }

        log.info("getById, event.getId()= {}, serviceEwn.saveEndpointHit(servletRequest) = {} ", event.getId(), serviceEwn.saveEndpointHit(servletRequest));

        event.setViews((long) httpServletRequests.get(eventId).size());
        Event eventWithView = eventRepoJpa.save(event);
        EventDto eventFullDto = EventMapper.toEventDto(eventWithView);
        log.debug("getById, eventId = {}, servletRequest = {}", eventId, servletRequest);

        return eventFullDto;
    }

    @Override
    public EventDto getByUserIdAndEventId(Long userId, Long eventId) {
        Event event = eventRepoJpa.findByUserIdAndEventId(userId, eventId);
        EventDto eventFullDto = EventMapper.toEventDto(event);
        log.debug("getByUserIdAndEventId, userId = {}, eventId = {}", userId, eventId);

        return eventFullDto;
    }

    @Override
    public List<ParticipationRequestDto> getByUserIdRequests(Long userId) {
        List<Request> requests = requestEventRepoJpa.findByRequesterId(userId);
        log.debug("getByUserIdRequests, userId = {} ", userId);
        return requests.stream().map(request -> getParticipationRequestDto(request)).collect(Collectors.toList());


    }

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId, Long eventId) {

        userRepoJpa.findById(userId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь с id = '" + userId + "' не найден"));
        eventRepoJpa.findById(eventId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Событие с id = '" + userId + "' не найдено"));

        List<Request> requests = requestEventRepoJpa.findAllByEvent_Id(eventId);
        log.debug("getRequests, userId = {}, eventId = {}", userId, eventId);
        List<ParticipationRequestDto> collect = requests.stream().map(request -> getParticipationRequestDto(request)).collect(Collectors.toList());
        return collect;
    }

    @Transactional
    public EventDto updateByAdmin(UpdateEventAdminRequest updateEventAdminRequest, Long eventId) {
        Event updateEvent = eventRepoJpa.findById(eventId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Событие c id = '" + eventId + "' не существует"));

        if (updateEventAdminRequest.getStateAction() != null && updateEventAdminRequest.getStateAction().equals(PUBLISH_EVENT)) {
            if (updateEvent.getState() != PENDING) {
                throw new ConflictException("Публикация уже опубликованного события.");
            }
        }
        if (updateEventAdminRequest.getStateAction() != null && updateEventAdminRequest.getStateAction() == REJECT_EVENT) {
            if (updateEvent.getState() == PUBLISHED) {
                throw new ConflictException("Публикация отклоненного события.");
            }
        }
        if (updateEventAdminRequest.getStateAction() != null && updateEvent.getState().equals(CANCELED) && updateEventAdminRequest.getStateAction().equals(PUBLISH_EVENT)) {
            throw new ConflictException("Публикация отмененного события.");
        }

        if (updateEventAdminRequest.getAnnotation() != null) {
            if (!updateEvent.getAnnotation().equals(updateEventAdminRequest.getAnnotation())) {
                updateEvent.setAnnotation(updateEventAdminRequest.getAnnotation());
                log.debug("updateByAdmin, было getAnnotation()= {}, стало getAnnotation() = {}", updateEvent.getAnnotation(), updateEventAdminRequest.getAnnotation());
            }
        }

        if (updateEventAdminRequest.getCategory() != null) {
            if (!updateEvent.getCategory().getId().equals(updateEventAdminRequest.getCategory())) {
                Category category = categoryRepoJpa.findById(updateEventAdminRequest.getCategory()).get();
                updateEvent.setCategory(category);
                log.debug("updateByAdmin, было getCategory()= {}, стало getCategory() = {}", updateEvent.getCategory(), category);

            }
        }

        if (updateEventAdminRequest.getDescription() != null) {
            if (!updateEvent.getDescription().equals(updateEventAdminRequest.getDescription())) {
                updateEvent.setDescription(updateEventAdminRequest.getDescription());
                log.debug("updateByAdmin, было getDescription()= {}, стало getDescription() = {}", updateEvent.getDescription(), updateEventAdminRequest.getDescription());

            }
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            if (updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new BadRequestException(HttpStatus.BAD_REQUEST, "Изменение даты события на уже наступившую");
            }
            updateEvent.setDate(updateEventAdminRequest.getEventDate());
            log.debug("updateByAdmin, было getEventDate()= {}, стало getEventDate() = {}", updateEvent.getDate(), updateEventAdminRequest.getEventDate());

        }

        if (updateEventAdminRequest.getLocation() != null) {
            if (Float.compare(updateEvent.getLocation().getLat(), updateEventAdminRequest.getLocation().getLat()) != 0 || Float.compare(updateEvent.getLocation().getLon(), updateEventAdminRequest.getLocation().getLon()) != 0) {
                updateEvent.setLocation(new Location(updateEventAdminRequest.getLocation().getLat(), updateEventAdminRequest.getLocation().getLon()));
                log.debug("updateByAdmin, было getLocation().getLat() = {}, стало getLocation().getLon() = {}", updateEvent.getLocation().getLat(), updateEventAdminRequest.getLocation().getLon());
            }
        }
        if (updateEventAdminRequest.getPaid() != null) {
            if (Boolean.compare(updateEvent.getPaid(), updateEventAdminRequest.getPaid()) != 0) {
                updateEvent.setPaid(updateEventAdminRequest.getPaid());
                log.debug("updateByAdmin, было getPaid()= {}, стало getPaid() = {}", updateEvent.getPaid(), updateEventAdminRequest.getPaid());

            }
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            if (!updateEvent.getParticipantLimit().equals(updateEventAdminRequest.getParticipantLimit())) {
                updateEvent.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
                log.debug("updateByAdmin, было getParticipantLimit()= {}, стало getParticipantLimit() = {}", updateEvent.getParticipantLimit(), updateEventAdminRequest.getParticipantLimit());
            }
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            if (Boolean.compare(updateEvent.getRequestModeration(), updateEventAdminRequest.getRequestModeration()) != 0) {
                updateEvent.setRequestModeration(updateEventAdminRequest.getRequestModeration());
                log.debug("updateByAdmin, было getRequestModeration()= {}, стало getRequestModeration() = {}", updateEvent.getRequestModeration(), updateEventAdminRequest.getRequestModeration());

            }
        }
        if (updateEventAdminRequest.getTitle() != null) {
            if (!updateEvent.getTitle().equals(updateEventAdminRequest.getTitle())) {
                updateEvent.setTitle(updateEventAdminRequest.getTitle());
                log.debug("updateByAdmin, было getTitle()= {}, стало getTitle() = {}", updateEvent.getTitle(), updateEventAdminRequest.getTitle());

            }
        }
        if (!updateEvent.getState().equals(updateEventAdminRequest.getStateAction())) {
            updateEvent.setState(updateEventAdminRequest.getStateAction());
            log.debug("updateByAdmin, было getState()= {}, стало getStateAction() = {}", updateEvent.getState(), updateEventAdminRequest.getStateAction());
        }

        if (updateEventAdminRequest.getStateAction() != null && updateEventAdminRequest.getStateAction().equals(PUBLISH_EVENT)) {
            updateEvent.setState(PUBLISHED);
            updateEvent.setPublishedOn(LocalDateTime.now());
            log.debug("updateByAdmin,  стало updateEvent = {}", updateEvent.getState());

        }
        validateItem(updateEvent);
        log.debug("Публикация обнавлена,updateEventAdminRequest = {},eventId = {}", updateEventAdminRequest, eventId);
        Event updatedItem = eventRepoJpa.save(updateEvent);
        return mapper.map(updatedItem, EventDto.class);
    }

    @Override
    @Transactional
    public EventDto updateByUser(UpdateEventUserRequest updateEventUserRequest, Long eventId, Long userId) {

        Event updateEvent = eventRepoJpa.findById(eventId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Событие c id = '" + eventId + "' не существует"));
        if (updateEvent.getState() == PUBLISHED) {
            throw new ConflictException("Изменение опубликованного события от имени пользователя");
        }
        userRepoJpa.findById(userId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь id = '" + userId + "' не существует"));

        if (updateEventUserRequest.getStateAction() != null) {
            if (!updateEvent.getState().equals(updateEventUserRequest.getStateAction())) {
                switch (updateEventUserRequest.getStateAction()) {
                    case CANCEL_REVIEW:
                        updateEvent.setState(CANCELED);
                        break;
                    case SEND_TO_REVIEW:
                        updateEvent.setState(PENDING);
                        break;
                }
            }
        }
        if (updateEvent.getState() == CANCELED || updateEvent.getState() == PENDING) {
            if (updateEventUserRequest.getEventDate() != null) {
                if (updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                    throw new BadRequestException(HttpStatus.BAD_REQUEST, "дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента");

                }
                updateEvent.setDate(updateEventUserRequest.getEventDate());
            }


            if (updateEventUserRequest.getAnnotation() != null) {
                if (!updateEvent.getAnnotation().equals(updateEventUserRequest.getAnnotation())) {
                    updateEvent.setAnnotation(updateEventUserRequest.getAnnotation());
                }
            }

            if (updateEventUserRequest.getCategory() != null) {
                if (updateEvent.getCategory().getId().equals(updateEventUserRequest.getCategory())) {
                    updateEvent.setCategory(categoryRepoJpa.findById(updateEventUserRequest.getCategory()).get());
                }
            }

            if (updateEventUserRequest.getDescription() != null) {
                if (!updateEvent.getDescription().equals(updateEventUserRequest.getDescription())) {
                    updateEvent.setDescription(updateEventUserRequest.getDescription());
                }
            }
            if (updateEventUserRequest.getEventDate() != null) {
                if (updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                    throw new BadRequestException(HttpStatus.BAD_REQUEST, "Изменение даты события на уже наступившую");
                }
                updateEvent.setDate(updateEventUserRequest.getEventDate());

            }
            if (updateEventUserRequest.getLocation() != null) {
                if (Float.compare(updateEvent.getLocation().getLat(), updateEventUserRequest.getLocation().getLat()) != 0 || Float.compare(updateEvent.getLocation().getLon(), updateEventUserRequest.getLocation().getLon()) != 0) {
                    updateEvent.setLocation(new Location(updateEventUserRequest.getLocation().getLat(), updateEventUserRequest.getLocation().getLon()));
                    updateEvent.setLocation(new Location(updateEventUserRequest.getLocation().getLat(), updateEventUserRequest.getLocation().getLon()));

                }
            }
            if (updateEventUserRequest.getPaid() != null) {
                if (Boolean.compare(updateEvent.getPaid(), updateEventUserRequest.getPaid()) != 0) {
                    updateEvent.setPaid(updateEventUserRequest.getPaid());
                }
            }
            if (updateEventUserRequest.getParticipantLimit() != null) {
                if (updateEvent.getParticipantLimit().equals(updateEventUserRequest.getParticipantLimit())) {
                    updateEvent.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
                }
            }
            if (updateEventUserRequest.getRequestModeration() != null) {
                if (Boolean.compare(updateEvent.getRequestModeration(), updateEventUserRequest.getRequestModeration()) != 0) {
                    updateEvent.setRequestModeration(updateEventUserRequest.getRequestModeration());
                }
            }
            if (updateEventUserRequest.getTitle() != null) {
                if (!updateEvent.getTitle().equals(updateEventUserRequest.getTitle())) {
                    updateEvent.setTitle(updateEventUserRequest.getTitle());
                }
            }

        } else {
            throw new BadRequestException(HttpStatus.CONFLICT, "Изменить можно только отмененное событие или событие в режиме ожидания");
        }


        validateItem(updateEvent);
        log.debug("Публикация обнавлена,updateEventUserRequest = {},eventId = {}, userId ={}", updateEventUserRequest, eventId, userId);
        Event updatedItem = eventRepoJpa.save(updateEvent);
        return mapper.map(updatedItem, EventDto.class);
    }

    @Override
    @Transactional
    public ParticipationRequestDto updateByUserCancel(Long userId, Long requestId) {

        Request request = requestEventRepoJpa.findById(requestId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Запрос с id = '" + requestId + "' не существует"));

        request.setStatus(Status.CANCELED);
        log.debug("updateByUserCancel, eventId = {}, requestId ={}", userId, requestId);
        Request requestUpdated = requestEventRepoJpa.save(request);
        return getParticipationRequestDto(requestUpdated);
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestStatus(EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest, Long userId, Long eventId) {
        if (eventRequestStatusUpdateRequest == null) {
            throw new ConflictException("Status is not validate");
        }
        userRepoJpa.findById(userId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь id = '" + userId + "' не существует"));

        EventRequestStatusUpdateResult eventRequestStatusUpdateResult = new EventRequestStatusUpdateResult();

        Event event = eventRepoJpa.findById(eventId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Событие c id = '" + eventId + "' не существует"));


        List<Long> requestIds = eventRequestStatusUpdateRequest.getRequestIds();


        for (Long requestId : requestIds) {
            Request request = requestEventRepoJpa.findById(requestId).get();

            switch (Status.valueOf(eventRequestStatusUpdateRequest.getStatus())) {
                case REJECTED:
                    if (!request.getStatus().equals(REJECTED)) {
                        if (request.getStatus().equals(CONFIRMED)) {
                            throw new ConflictException("Попытка отменить уже принятую заявку на участие в событии");
                        }
                        request.setStatus(REJECTED);
                        eventRequestStatusUpdateResult.getRejectedRequests().add(getParticipationRequestDto(request));
                        requestEventRepoJpa.save(request);
                    } else {
                        throw new ConflictException("Попытка отменить уже принятую заявку на участие в событии");

                    }

                    break;
                case CONFIRMED:
                    Long participantLimit = event.getParticipantLimit();
                    Long approvedRequests = event.getConfirmedRequests();
                    long availableParticipants = participantLimit - approvedRequests;
                    long potentialParticipants = requestIds.size();

                    if (participantLimit > 0 && participantLimit.equals(approvedRequests)) {
                        throw new ConflictException("Превышен лимит на участие в мероприятии");
                    }
                    if (participantLimit.equals(0L) || (potentialParticipants <= availableParticipants)) {

                        request.setStatus(CONFIRMED);
                        eventRequestStatusUpdateResult.getConfirmedRequests().add(getParticipationRequestDto(request));
                        requestEventRepoJpa.save(request);
                        long confirmedRequests = event.getConfirmedRequests();
                        event.setConfirmedRequests(confirmedRequests + 1L);
                        eventRepoJpa.save(event);
                    }
                    break;
            }
        }
        log.debug("in patchByUserIdAndEventIdRequests, eventRequestStatusUpdateRequest = {} userId = {}, eventId ={}", eventRequestStatusUpdateRequest, userId, eventId);
        log.debug("out patchByUserIdAndEventIdRequests, eventRequestStatusUpdateResult = {}  ", eventRequestStatusUpdateResult);
        return eventRequestStatusUpdateResult;
    }

    @Override
    public List<EventDto> getAllAdmin(RequestParamAdmin param) {

        LocalDateTime rangeEndLocalDateTime = param.getRangeEnd();
        LocalDateTime rangeStartLocalDateTime = param.getRangeStart();

        if (rangeStartLocalDateTime != null && rangeEndLocalDateTime != null) {
            if (rangeEndLocalDateTime.isBefore(rangeStartLocalDateTime)) {
                throw new BadRequestException(HttpStatus.BAD_REQUEST, "Конечная дата должна быть ранее начальной");
            }
        }

        Pageable pageable = PageRequest.of(param.getFrom() / param.getSize(), param.getSize(), org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "id"));

        CriteriaAdmin criteria = CriteriaAdmin.builder()
                .users(param.getUsers())
                .states(param.getStates())
                .categories(param.getCategories())
                .rangeEnd(rangeEndLocalDateTime)
                .rangeStart(rangeStartLocalDateTime)
                .build();

        List<Event> events1 = eventRepoJpa.findByParamAdmin(pageable, criteria).toList();
        events1.stream()
                .map(EventMapper::toEventDto)
                .collect(Collectors.toList());

        List<EventDto> events = events1.stream().map(e -> mapper.map(e, EventDto.class)).collect(Collectors.toList());

        log.info("getAllAdmin, events.size() {}", events.size());
        return events;
    }

    public Set<EventShortDto> getAllPublic(RequestParamUser param) {

        LocalDateTime rangeEndLocalDateTime = param.getRangeEnd();
        LocalDateTime rangeStartLocalDateTime = param.getRangeStart();

        if (rangeStartLocalDateTime != null && rangeEndLocalDateTime != null) {
            if (rangeEndLocalDateTime.isBefore(rangeStartLocalDateTime)) {
                throw new BadRequestException(HttpStatus.BAD_REQUEST, "Конечная дата должна быть ранее начальной");
            }
        }

        Pageable pageable = PageRequest.of(param.getFrom() / param.getSize(), param.getSize(), org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "id"));

        CriteriaUser criteriaUser = CriteriaUser.builder().text(param.getText()).categories(param.getCategories()).rangeEnd(rangeEndLocalDateTime).rangeStart(rangeStartLocalDateTime).paid(param.getPaid()).build();

        List<Event> events1 = eventRepoJpa.findByParamUser(pageable, criteriaUser).toList();
        List<EventShortDto> events2 = events1.stream().map(e -> mapper.map(e, EventShortDto.class)).collect(Collectors.toList());

        Set<EventShortDto> events = new HashSet<>(events2);
        log.info("  {}", events.size());

        HttpServletRequest request = param.getRequest();
//        EndpointHitDto endpointHit = EndpointHitDto.builder().ip(request.getRemoteAddr()).uri(request.getRequestURI()).app(serviceName).timestamp(LocalDateTime.now()).build();
//        statsClient.saveStats(endpointHit);
        log.info("getAllPublic (save stats), events.size()= {}, serviceEwn.saveEndpointHit(servletRequest) = {} ", events.size(), serviceEwn.saveEndpointHit(request));
        return events;
    }

    private static ParticipationRequestDto getParticipationRequestDto(Request request) {

        ParticipationRequestDto participationRequestDto;
        participationRequestDto = ParticipationRequestDto.builder().id(request.getId()).created(request.getCreated()).event(request.getEvent().getId()).requester(request.getRequester().getId()).status(request.getStatus()).build();
        log.debug("in getParticipationRequestDto(mapper reauest->requestDto), request = {}  ", request);
        log.debug("out getParticipationRequestDto(mapper reauest->requestDto), response = {}  ", participationRequestDto);
        return participationRequestDto;
    }


}
