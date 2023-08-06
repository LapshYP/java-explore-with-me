package ru.practicum.ewmservice.category.service;


import ru.practicum.ewmservice.category.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {
    CategoryDTO create (CategoryDTO categoryDTO);

    List<CategoryDTO> getAll(int from, int size);

    CategoryDTO update(CategoryDTO categoryDTO, Long userId);

    CategoryDTO delete(Long categoryId);

    CategoryDTO get(Long categoryId);

}
