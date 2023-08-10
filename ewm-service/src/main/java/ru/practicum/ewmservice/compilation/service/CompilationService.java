package ru.practicum.ewmservice.compilation.service;


import ru.practicum.ewmservice.compilation.dto.CompilationDto;
import ru.practicum.ewmservice.compilation.dto.CompilationWithEventsDto;

import java.util.List;

public interface CompilationService {
    CompilationWithEventsDto create (CompilationDto compilationDTO);

    List<CompilationWithEventsDto> getAll(int from, int size);

    CompilationDto update(CompilationDto compilationDTO, Long compId);

    CompilationDto delete(Long compId);

    CompilationDto get(Long compId);

}
