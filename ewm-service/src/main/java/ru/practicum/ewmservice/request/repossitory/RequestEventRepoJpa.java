package ru.practicum.ewmservice.request.repossitory;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmservice.request.model.Request;

import java.util.List;

@Repository
public interface RequestEventRepoJpa extends JpaRepository<Request, Long> {
    List<Request> findByRequesterId(Long requesterId);

    List<Request> findByIdAndRequesterId(Long eventId, Long userId);

    List<Request> findAllByEvent_Id(Long eventId);
@Query(value = "SELECT COUNT(status) FROM requests WHERE status = 'PENDING' and EVENT_ID=?1",nativeQuery = true)
    Long countParticipantLimit(Long eventId);

    @Query(value = "SELECT COUNT(EVENT_ID) FROM requests WHERE  EVENT_ID=?1",nativeQuery = true)
    Long getСonfirmedRequests(Long eventId);

    List<Request> findAllByIdIn(List<Long> requestIds);

//@Query(value = "SELECT * FROM EVENTS WHERE ID=:eventId AND INITIATOR_ID=:userId",nativeQuery = true)
//    List<Request> findByRequesterIdAndId(Long userId, Long eventId);

//    List<Request> findAllByRequester_Id(Long userId);
//
//    @Query("SELECT i FROM Request i WHERE i.requester.id <> ?1 ORDER BY i.createdtime DESC")
//    List<Request> findByOwnerId(Long userId, Pageable pageable);

}
