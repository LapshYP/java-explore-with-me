package ru.practicum.ewmservice.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmservice.user.model.User;


import java.util.List;


public interface UserRepoJpa extends JpaRepository<User, Long> {

    List<User> findByIdAndEmail(Long id, String email);


}
