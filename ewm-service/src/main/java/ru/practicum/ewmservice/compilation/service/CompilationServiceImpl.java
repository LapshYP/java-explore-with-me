package ru.practicum.ewmservice.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.compilation.dto.CompilationDto;
import ru.practicum.ewmservice.compilation.dto.CompilationWithIdAndEventsDto;
import ru.practicum.ewmservice.compilation.model.Compilation;
import ru.practicum.ewmservice.compilation.repository.CompilationRepoJpa;
import ru.practicum.ewmservice.event.model.Event;
import ru.practicum.ewmservice.event.repository.EventRepoJpa;
import ru.practicum.ewmservice.exception.BadRequestException;
import ru.practicum.ewmservice.exception.NotFoundException;

import javax.validation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepoJpa compilationRepoJpa;

    private final EventRepoJpa eventRepoJpa;


    private final ModelMapper mapper = new ModelMapper();

    private void validateUser(Compilation compilation) {
        Validator validator;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        Set<ConstraintViolation<Compilation>> violations = validator.validate(compilation);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    @SneakyThrows
    @Override
    @Transactional
    public CompilationWithIdAndEventsDto create(CompilationDto compilationDTO) {
        Compilation compilation = mapper.map(compilationDTO, Compilation.class);
        if (compilation.getTitle() == null) {
            throw new BadRequestException(HttpStatus.BAD_REQUEST, "Title field equal null");
        }
        if (compilation.getTitle().length() > 50) {
            throw new BadRequestException(HttpStatus.BAD_REQUEST, "Title field must be <50");
        }

        List<Event> events = new ArrayList<>();
        if (compilationDTO.getEvents() != null && !compilationDTO.getEvents().isEmpty()) {
            events = eventRepoJpa.findAllById(compilationDTO.getEvents());
        }

        compilation.setEvents(events);
        compilation.setPinned(false);

        validateUser(compilation);
        Compilation savedCompilation = compilationRepoJpa.save(compilation);
        log.debug("Подборка добавлена,  id ={}  ", compilation.getId());
        return mapper.map(savedCompilation, CompilationWithIdAndEventsDto.class);
    }

    @Override
    public List<CompilationWithIdAndEventsDto> getAll(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        log.debug("getAll,  from ={}, size ={}  ", from, size);
        return compilationRepoJpa.findAll(pageable).stream().map(compilation -> {
            return mapper.map(compilation, CompilationWithIdAndEventsDto.class);
        }).collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    @Transactional
    public CompilationDto update(CompilationDto compilationWithEventsDto, Long catId) {
        Compilation compilation = mapper.map(compilationWithEventsDto, Compilation.class);
        Compilation updatedCompilation = compilationRepoJpa.findById(catId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь с id = '" + catId + "' не найден"));
        if (compilation.getTitle() != null) {
            updatedCompilation.setTitle(compilation.getTitle());
        }
        if (compilation.getTitle() != null) {
            if (compilation.getTitle().length() > 50) {
                throw new BadRequestException(HttpStatus.BAD_REQUEST, "Name field must be <50");
            }
        }
        if (compilation.getPinned() != null) {
            updatedCompilation.setPinned(compilation.getPinned());
        }


        List<Event> events = new ArrayList<>();
        if (compilationWithEventsDto.getEvents() != null && !compilationWithEventsDto.getEvents().isEmpty()) {
            events = eventRepoJpa.findAllById(compilationWithEventsDto.getEvents());
        }


        updatedCompilation.setEvents(events);

        validateUser(updatedCompilation);
        Compilation saveCompilation = compilationRepoJpa.save(updatedCompilation);
        log.debug("Подборка обновлена, id = {} ", compilation.getId());

        return CompilationDto.builder()
                .id(saveCompilation.getId())
                .title(saveCompilation.getTitle())
                .pinned(saveCompilation.getPinned())
                .events(compilationWithEventsDto.getEvents())
                .build();
    }

    @Override
    public CompilationDto delete(Long catId) {
        Compilation saveCompilation = compilationRepoJpa.findById(catId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь с id = '" + catId + "' не найден"));

        compilationRepoJpa.deleteById(catId);
        log.debug("Категория удалена, catId  = {} ", catId);

        return CompilationDto.builder()
                .id(saveCompilation.getId())
                .title(saveCompilation.getTitle())
                .pinned(saveCompilation.getPinned())
                .build();
    }

    @Override
    public CompilationWithIdAndEventsDto getCompilations(Long catId) {
        Compilation compilation = compilationRepoJpa.findById(catId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Категория с id = '" + catId + "' не найдена"));
        CompilationWithIdAndEventsDto compilationDTO = mapper.map(compilation, CompilationWithIdAndEventsDto.class);
        log.debug("Категория просмотрена, catId= {}  ", catId);
        return compilationDTO;
    }
}