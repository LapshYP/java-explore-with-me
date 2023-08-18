package ru.practicum.ewmservice.request.service;

import org.springframework.stereotype.Service;
import ru.practicum.ewmservice.request.dto.RequestDto;

import java.util.List;

@Service

public interface RequestEventService {

    RequestDto create(Long eventId, Long userId);

    List<RequestDto> requestsGet(Long userId);

    List<RequestDto> requestsAllGet(Long userId, int from, int size);


    RequestDto getRequestById(Long userId, Long requestId);

}
