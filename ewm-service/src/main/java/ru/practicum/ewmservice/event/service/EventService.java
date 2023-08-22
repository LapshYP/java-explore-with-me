package ru.practicum.ewmservice.event.service;

import org.springframework.stereotype.Service;
import ru.practicum.ewmservice.event.dto.*;
import ru.practicum.ewmservice.event.model.RequestParamAdmin;
import ru.practicum.ewmservice.event.model.RequestParamUser;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

@Service
public interface EventService {

    EventDto create(EventNewDto eventNewDto, Long userId);

    List<EventDto> getByUserId(Long userId, int from, int size);

    EventDto getById(Long eventId, HttpServletRequest servletRequest);

    EventDto getByUserIdAndEventId(Long userId, Long eventId);

    List<ParticipationRequestDto> getByUserIdRequests(Long userId);

    List<ParticipationRequestDto> getRequests(Long userId, Long eventId);

    EventDto updateByAdmin(UpdateEventAdminRequest updateEventAdminRequest, Long eventId);

    EventDto updateByUser(UpdateEventUserRequest updateEventUserRequest, Long eventId, Long userId);

    ParticipationRequestDto updateByUserCancel(Long userId, Long requestId);

    EventRequestStatusUpdateResult updateRequestStatus(EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest, Long userId, Long eventId);

    List<EventDto> getAllAdmin(RequestParamAdmin param);

    Set<EventShortDto> getAllPublic(RequestParamUser param);

}
