package ru.practicum.ewmservice.event.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmservice.event.model.Event;

import java.util.List;

@Repository
public interface CommentRepoJpa extends JpaRepository<Comment, Long> {


}