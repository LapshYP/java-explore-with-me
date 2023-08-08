package ru.practicum.ewmservice.category;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.category.dto.CategoryDto;
import ru.practicum.ewmservice.category.service.CategoryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping(path = "/admin/categories")
    public ResponseEntity <CategoryDto> createUser(@RequestBody CategoryDto categoryDTO) {

        return new ResponseEntity(categoryService.create(categoryDTO), HttpStatus.CREATED);
    }

    @PatchMapping("/admin/categories/{catId}")
    public CategoryDto update(@RequestBody CategoryDto categoryDTO, @PathVariable Long catId) {
        return categoryService.update(categoryDTO, catId);
    }

    @DeleteMapping("/admin/categories/{catId}")
    public ResponseEntity <CategoryDto> delete(@PathVariable Long catId) {

        return new ResponseEntity( categoryService.delete(catId), HttpStatus.valueOf(204));
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto get(@PathVariable Long catId) {
        return categoryService.get(catId);
    }

    @GetMapping(path = "/categories")
    public List<CategoryDto> getAll(@RequestParam(name = "from", defaultValue = "0") int from,
                                    @RequestParam(name = "size", defaultValue = "10") int size) {
        return categoryService.getAll(from,size);
    }
}
