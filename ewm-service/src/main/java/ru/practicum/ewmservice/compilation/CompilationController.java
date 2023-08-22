package ru.practicum.ewmservice.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.compilation.dto.CompilationDto;
import ru.practicum.ewmservice.compilation.dto.CompilationWithIdAndEventsDto;
import ru.practicum.ewmservice.compilation.service.CompilationService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CompilationController {

    private final CompilationService compilationService;

    @PostMapping(path = "/admin/compilations")
    public ResponseEntity<CompilationWithIdAndEventsDto> create(@RequestBody CompilationDto compilationDTO) {
        log.info("create POST /admin/compilations , compilationDTO = {}", compilationDTO);

        return new ResponseEntity(compilationService.create(compilationDTO), HttpStatus.CREATED);
    }

    @PatchMapping("/admin/compilations/{compId}")
    public CompilationDto update(@RequestBody @Valid CompilationDto compilationDto,
                                 @PathVariable Long compId) {
        log.info("update PATCH /admin/compilations/{compId} , compilationDTO = {}, compId = {}", compilationDto, compId);
        return compilationService.update(compilationDto, compId);
    }

    @DeleteMapping("/admin/compilations/{compId}")
    public ResponseEntity<CompilationDto> delete(@PathVariable Long compId) {
        log.info("delete DELETE /admin/compilations/{compId}, compId = {}", compId);

        return new ResponseEntity(compilationService.delete(compId), HttpStatus.valueOf(204));
    }

    @GetMapping("/compilations/{catId}")
    public CompilationWithIdAndEventsDto getCompilations(@PathVariable Long catId) {
        log.info("getCompilations GET compilations/{catId}, compId = {}", catId);
        return compilationService.getCompilations(catId);
    }

    @GetMapping(path = "/compilations")
    public List<CompilationWithIdAndEventsDto> getAll(@RequestParam(name = "from", defaultValue = "0") int from,
                                                      @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("getAll GET /compilations, from = {}, size = {}", from, size);

        return compilationService.getAll(from, size);
    }
}
