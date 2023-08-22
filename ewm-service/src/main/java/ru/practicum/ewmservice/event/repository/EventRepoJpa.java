package ru.practicum.ewmservice.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmservice.event.model.Event;
import ru.practicum.ewmservice.event.model.State;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepoJpa extends JpaRepository<Event, Long>, EventCriteriaRepository {

    @Query(value = "SELECT * FROM EVENTS WHERE INITIATOR_ID   = ?1", nativeQuery = true)
    List<Event> findByUserId(Long userId, Pageable pageable);

    @Query(value = "SELECT * FROM EVENTS WHERE INITIATOR_ID   =  :userId AND  ID = :eventId", nativeQuery = true)
    Event findByUserIdAndEventId(Long userId, Long eventId);

    @Query(value = "SELECT * FROM EVENTS WHERE CATEGORY_ID = ?1", nativeQuery = true)
    List<Event> findByCategoryId(Long catId);


    @Query("SELECT event FROM Event event " +
            "WHERE event.initiator.id IN (:users) " +
            "AND event.state IN (:states) " +
            "AND event.category.id IN (:categories) " +
            "AND event.date BETWEEN :rangeStart " +
            "AND :rangeEnd")
    List<Event> findEventsByParams(
            @Param("users") List<Long> users,
            @Param("states") List<State> states,
            @Param("categories") List<Long> categories,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageable);

    @Query("SELECT event FROM Event event " +
            "WHERE event.category.id IN (:categories)")
    List<Event> findByParam(@Param("categories") List<Long> categories, Pageable pageable);

}
