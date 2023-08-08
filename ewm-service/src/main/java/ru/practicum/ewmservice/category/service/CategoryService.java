package ru.practicum.ewmservice.category.service;


import ru.practicum.ewmservice.category.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto create (CategoryDto categoryDTO);

    List<CategoryDto> getAll(int from, int size);

    CategoryDto update(CategoryDto categoryDTO, Long userId);

    CategoryDto delete(Long categoryId);

    CategoryDto get(Long categoryId);

}
