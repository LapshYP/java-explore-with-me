package ru.practicum.ewmservice.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmservice.category.model.Category;


public interface CategoryRepoJpa extends JpaRepository<Category, Long> {


}
