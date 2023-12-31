package ru.practicum.ewmservice.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.category.dto.CategoryDto;
import ru.practicum.ewmservice.category.service.CategoryService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping(path = "/admin/categories")
    public ResponseEntity<CategoryDto> createUser(@RequestBody CategoryDto categoryDTO) {
        log.info("create POST /admin/categories , categoryDTO = {}", categoryDTO);

        return new ResponseEntity(categoryService.create(categoryDTO), HttpStatus.CREATED);
    }

    @PatchMapping("/admin/categories/{catId}")
    public CategoryDto update(@RequestBody @Valid CategoryDto categoryDTO, @PathVariable Long catId) {
        log.info("update PATCH /admin/categories/{catId} , categoryDTO = {} , catId = {}", categoryDTO, catId);
        return categoryService.update(categoryDTO, catId);
    }

    @DeleteMapping("/admin/categories/{catId}")
    public ResponseEntity<CategoryDto> delete(@PathVariable Long catId) {
        log.info("delete DELETE /admin/categories/{catId} , catId = {}", catId);
        return new ResponseEntity(categoryService.delete(catId), HttpStatus.valueOf(204));
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getCategories(@PathVariable Long catId) {
        log.info("getCategories GET /categories/{catId} , catId = {}", catId);
        return categoryService.getCategories(catId);
    }

    @GetMapping(path = "/categories")
    public List<CategoryDto> getAll(@RequestParam(name = "from", defaultValue = "0") int from,
                                    @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("getAll GET /categories , from = {}, size = {}", from, size);
        return categoryService.getAll(from, size);
    }
}
