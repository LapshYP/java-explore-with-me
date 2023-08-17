package ru.practicum.ewmservice.event.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmservice.event.model.Event;
import ru.practicum.ewmservice.event.model.EventSearchCriteria;


@Repository
public interface EventCriteriaRepository {

    Page<Event> findByParamFilters(Pageable pageable, EventSearchCriteria eventSearchCriteria);
}