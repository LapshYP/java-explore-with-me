package ru.practicum.ewmservice.comment.repository;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmservice.comment.model.Comment;

import java.util.List;

@Repository
public interface CommentRepoJpa extends JpaRepository<Comment, Long> {

    List<Comment> getByEvent_Id(Long eventId, Pageable pageable);

    @Modifying
    @Query("delete from Comment c where c.id=:comId")
    void deleteComment(Long comId);

    @Query(value = " SELECT COUNT(events.ID) FROM events LEFT JOIN comments ON events.ID = comments.event_id WHERE events.id=?1", nativeQuery = true)
    Long getCommentsCountByEventId(Long eventId);

    @Query(value = " SELECT COUNT(events.ID) FROM events LEFT JOIN comments ON events.ID = comments.event_id WHERE events.id=?1 AND comments.id=?2 AND comments.author_id=?3", nativeQuery = true)
    Long getCommentsCountByEventIdAndUserId(Long eventId,Long comId, Long userId);
}