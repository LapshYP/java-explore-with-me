package ru.practicum.ewmservice.event.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmservice.event.model.CriteriaAdmin;
import ru.practicum.ewmservice.event.model.Event;
import ru.practicum.ewmservice.event.model.CriteriaUser;

import java.util.stream.Collectors;


@Repository
public interface EventCriteriaRepository {

    Page<Event> findByParamUser(Pageable pageable, CriteriaUser criteriaUser);

    Page<Event> findByParamAdmin(Pageable pageable, CriteriaAdmin criteria);
}