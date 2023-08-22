package ru.practicum.ewmservice.event.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepoJpa extends JpaRepository<Comment, Long> {


}