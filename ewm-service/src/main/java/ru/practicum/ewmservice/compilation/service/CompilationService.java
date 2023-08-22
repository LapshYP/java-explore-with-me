package ru.practicum.ewmservice.compilation.service;


import ru.practicum.ewmservice.compilation.dto.CompilationDto;
import ru.practicum.ewmservice.compilation.dto.CompilationWithIdAndEventsDto;

import java.util.List;

public interface CompilationService {
    CompilationWithIdAndEventsDto create(CompilationDto compilationDTO);

    List<CompilationWithIdAndEventsDto> getAll(int from, int size);

    CompilationDto update(CompilationDto compilationDto, Long compId);

    CompilationDto delete(Long compId);

    CompilationWithIdAndEventsDto getCompilations(Long compId);

}
