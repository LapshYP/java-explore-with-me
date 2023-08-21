package ru.practicum.ewmservice.request.repossitory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmservice.request.model.Request;

import java.util.List;

@Repository
public interface RequestEventRepoJpa extends JpaRepository<Request, Long> {
    List<Request> findByRequesterId(Long requesterId);

    List<Request> findAllByEvent_Id(Long eventId);

    @Query(value = "SELECT COUNT(status) FROM requests WHERE status = 'PENDING' and EVENT_ID=?1", nativeQuery = true)
    Long countParticipantLimit(Long eventId);

    @Query(value = "SELECT COUNT(EVENT_ID) FROM requests WHERE  EVENT_ID=?1", nativeQuery = true)
    Long getConfirmedRequests(Long eventId);

    @Query("SELECT request From Request request WHERE request.id IN(:requestids)")
    List<Request> findByIds(@Param("requestids") List<Long> requestIds);
}
