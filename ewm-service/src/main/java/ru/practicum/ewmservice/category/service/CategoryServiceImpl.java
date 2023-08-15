package ru.practicum.ewmservice.category.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.category.dto.CategoryDto;
import ru.practicum.ewmservice.category.model.Category;
import ru.practicum.ewmservice.category.repository.CategoryRepoJpa;
import ru.practicum.ewmservice.event.repository.EventRepoJpa;
import ru.practicum.ewmservice.exception.BadRequestException;
import ru.practicum.ewmservice.exception.NotFoundException;
import ru.practicum.ewmservice.exception.ConflictException;

import javax.validation.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final EventRepoJpa eventRepoJpa;

    private final CategoryRepoJpa categoryRepoJpa;

    private final ModelMapper mapper = new ModelMapper();

    private void validateUser(Category category) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Category>> violations = validator.validate(category);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    @SneakyThrows
    @Override
    public CategoryDto create(CategoryDto categoryDTO) {
        Category category = mapper.map(categoryDTO, Category.class);
        validateUser(category);
        if (category.getName().length() > 50) {
            throw new BadRequestException(HttpStatus.BAD_REQUEST, "Name field must be <50");
        }

        Category savedCategory = categoryRepoJpa.save(category);
        log.debug("create, id ={}, name = {}", category.getId(),category.getName());
        CategoryDto savedCategoryDto = mapper.map(savedCategory, CategoryDto.class);
        return savedCategoryDto;
    }

    @Override
    public List<CategoryDto> getAll(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);

        log.debug("данные в методе getAll запрошены, from ={}, size = {}", from,size);

        return categoryRepoJpa.findAll(pageable).stream().map(category -> {
            return mapper.map(category, CategoryDto.class);
        }).collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    @Transactional
    public CategoryDto update(CategoryDto categoryDTO, Long catId) {
        Category category = mapper.map(categoryDTO, Category.class);
        Category updatedCategory = categoryRepoJpa.findById(catId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь с id = '" + catId + "' не найден"));
        if (category.getName() != null) {
            updatedCategory.setName(category.getName());
        }
        if (category.getName().length() > 50) {
            throw new BadRequestException(HttpStatus.BAD_REQUEST, "Name field must be <50");
        }
        updatedCategory.setId(catId);
        validateUser(updatedCategory);
        categoryRepoJpa.save(updatedCategory);
        log.debug("Категория обновлена id = {}, name = {}",category.getId(), category.getName());
        CategoryDto updatedCategoryDto = mapper.map(updatedCategory, CategoryDto.class);
        return updatedCategoryDto;
    }

    @Override
    public CategoryDto delete(Long catId) {
        Category category = categoryRepoJpa.findById(catId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь с id = '" + catId + "' не найден"));
        CategoryDto categoryDTO = mapper.map(category, CategoryDto.class);
        if (eventRepoJpa.findByCategoryId(catId).size()>0) {
            throw new ConflictException("Удаление категории с привязанными событиями");
        }
        categoryRepoJpa.deleteById(catId);
        log.debug("Категория удалена, categoryId = {} ", catId);
        return categoryDTO;
    }

    @Override
    public CategoryDto getCategories(Long catId) {
        Category category = categoryRepoJpa.findById(catId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Категория с id = '" + catId + "' не найдена"));
        CategoryDto categoryDTO = mapper.map(category, CategoryDto.class);
        log.debug("Категория просмотрена, categoryId = {}  ", catId);
        return categoryDTO;
    }
}