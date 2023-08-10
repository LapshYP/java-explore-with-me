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
import ru.practicum.ewmservice.compilation.dto.CompilationWithEventsDto;
import ru.practicum.ewmservice.compilation.mapper.CompilationMapper;
import ru.practicum.ewmservice.compilation.model.Compilation;
import ru.practicum.ewmservice.compilation.repository.CompilationRepoJpa;
import ru.practicum.ewmservice.event.dto.EventDto;
import ru.practicum.ewmservice.event.model.Event;
import ru.practicum.ewmservice.event.repository.EventRepoJpa;
import ru.practicum.ewmservice.exception.BadRequestException;
import ru.practicum.ewmservice.exception.NotFoundException;

import javax.validation.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final ModelMapper modelMapper = new ModelMapper();

    private final CompilationRepoJpa compilationRepoJpa;
    private final EventRepoJpa eventRepoJpa;
    private    CompilationMapper compilationMapper;

    private final ModelMapper mapper = new ModelMapper();

    private void validateUser(Compilation compilation) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Compilation>> violations = validator.validate(compilation);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    @SneakyThrows
    @Override
    public CompilationWithEventsDto create(CompilationDto compilationDTO) {
        Compilation compilation = mapper.map(compilationDTO, Compilation.class);
        validateUser(compilation);
        if (compilation.getTitle().length() > 50) {
            throw new BadRequestException(HttpStatus.BAD_REQUEST, "Name field must be <50");
        }

        compilation.setPinned(false);
       Compilation savedCompilation = compilationRepoJpa.save(compilation);
        log.debug("Подборка с именем {} добавлена", compilation.getTitle());

     CompilationWithEventsDto savedCompilationDto = mapper.map(savedCompilation, CompilationWithEventsDto.class);
   //     CompilationWithEventsDto savedCompilationDto = compilationMapper.toDto(savedCompilation);
       if (compilationDTO.getEvents() != null){
           Set<Long> longs = compilationDTO.getEvents() ;
           Set<EventDto> events = longs.stream().map(id -> eventRepoJpa.findById(id).get()).map((element) -> modelMapper.map(element, EventDto.class)).collect(Collectors.toSet());
           savedCompilationDto.setEvents(events)  ;
       }

        return savedCompilationDto;
    }

    @Override
    public List<CompilationDto> getAll(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return compilationRepoJpa.findAll(pageable).stream().map(compilation -> {
            return mapper.map(compilation, CompilationDto.class);
        }).collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    @Transactional
    public CompilationDto update(CompilationDto compilationDTO, Long catId) {
        Compilation compilation = mapper.map(compilationDTO, Compilation.class);
        Compilation updatedCompilation = compilationRepoJpa.findById(catId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь с id = '" + catId + "' не найден"));
        if (compilation.getTitle() != null) {
            updatedCompilation.setTitle(compilation.getTitle());
        }
        if (compilation.getTitle().length() > 50) {
            throw new BadRequestException(HttpStatus.BAD_REQUEST, "Name field must be <50");
        }
        updatedCompilation.setId(catId);
        validateUser(updatedCompilation);
        compilationRepoJpa.save(updatedCompilation);
        log.debug("Подборка с именем {} обновлена", compilation.getTitle());
        CompilationDto updatedCompilationDto = mapper.map(updatedCompilation, CompilationDto.class);
        return updatedCompilationDto;
    }

    @Override
    public CompilationDto delete(Long catId) {
        Compilation compilation = compilationRepoJpa.findById(catId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь с id = '" + catId + "' не найден"));
        CompilationDto compilationDTO = mapper.map(compilation, CompilationDto.class);
        compilationRepoJpa.deleteById(catId);
        log.debug("Категория с categoryId = {} удалена", catId);
        return compilationDTO;
    }

    @Override
    public CompilationDto get(Long catId) {
        Compilation compilation = compilationRepoJpa.findById(catId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Категория с id = '" + catId + "' не найдена"));
        CompilationDto compilationDTO = mapper.map(compilation, CompilationDto.class);
        log.debug("Категория с categoryId = {} просмотрена", catId);
        return compilationDTO;
    }
}