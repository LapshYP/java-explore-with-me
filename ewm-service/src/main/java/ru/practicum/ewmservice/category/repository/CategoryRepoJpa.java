package ru.practicum.ewmservice.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmservice.category.model.Category;

@Repository
public interface CategoryRepoJpa extends JpaRepository<Category, Long> {


}
