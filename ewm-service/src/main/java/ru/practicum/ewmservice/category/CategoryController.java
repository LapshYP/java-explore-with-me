package ru.practicum.ewmservice.category;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.category.dto.CategoryDTO;
import ru.practicum.ewmservice.category.service.CategoryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping(path = "/admin/categories")
    public ResponseEntity <CategoryDTO> createUser(@RequestBody CategoryDTO categoryDTO) {

        return new ResponseEntity(categoryService.create(categoryDTO), HttpStatus.CREATED);
    }

    @PatchMapping("/admin/categories/{catId}")
    public CategoryDTO update(@RequestBody CategoryDTO categoryDTO, @PathVariable Long catId) {
        return categoryService.update(categoryDTO, catId);
    }

    @DeleteMapping("/admin/categories/{catId}")
    public ResponseEntity <CategoryDTO> delete(@PathVariable Long catId) {

        return new ResponseEntity( categoryService.delete(catId), HttpStatus.valueOf(204));
    }

    @GetMapping("/categories/{catId}")
    public CategoryDTO get(@PathVariable Long catId) {
        return categoryService.get(catId);
    }

    @GetMapping(path = "/categories")
    public List<CategoryDTO> getAll(@RequestParam(name = "from", defaultValue = "0") int from,
                                    @RequestParam(name = "size", defaultValue = "10") int size) {
        return categoryService.getAll(from,size);
    }
}
