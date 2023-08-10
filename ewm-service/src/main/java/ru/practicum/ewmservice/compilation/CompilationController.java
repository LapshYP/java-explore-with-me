package ru.practicum.ewmservice.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.compilation.dto.CompilationDto;
import ru.practicum.ewmservice.compilation.dto.CompilationWithEventsDto;
import ru.practicum.ewmservice.compilation.service.CompilationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CompilationController {

    private final CompilationService compilationService;

    @PostMapping(path = "/admin/compilations")
    public ResponseEntity <CompilationWithEventsDto> create(@RequestBody CompilationDto compilationDTO) {

        return new ResponseEntity(compilationService.create(compilationDTO), HttpStatus.CREATED);
    }

    @PatchMapping("/admin/compilations/{compId}")
    public CompilationDto update(@RequestBody CompilationDto compilationDTO, @PathVariable Long compId) {
        return compilationService.update(compilationDTO, compId);
    }

    @DeleteMapping("/admin/compilations/{compId}")
    public ResponseEntity <CompilationDto> delete(@PathVariable Long compId) {

        return new ResponseEntity( compilationService.delete(compId), HttpStatus.valueOf(204));
    }

    @GetMapping("/compilations/{catId}")
    public CompilationDto get(@PathVariable Long catId) {
        return compilationService.get(catId);
    }

    @GetMapping(path = "/compilations")
    public List<CompilationWithEventsDto> getAll(@RequestParam(name = "from", defaultValue = "0") int from,
                                       @RequestParam(name = "size", defaultValue = "10") int size) {
        return compilationService.getAll(from,size);
    }
}
