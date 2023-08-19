package ru.practicum.statsserver.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.statsdto.EndpointHitDto;
import ru.practicum.statsdto.ViewStatsDto;
import ru.practicum.statsserver.exception.BadRequestException;
import ru.practicum.statsserver.mapper.EndpointHitMapper;
import ru.practicum.statsserver.model.EndpointHit;
import ru.practicum.statsserver.repo.StatsRepo;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepo statsRepo;
    private final ModelMapper mapper = new ModelMapper();

    @Override
    public EndpointHitDto saveStats(EndpointHitDto endpointHitDto) {

        EndpointHit endpointHit = EndpointHitMapper.toEntity(endpointHitDto);
        EndpointHit hit = statsRepo.save(endpointHit);
        EndpointHitDto hitDto = mapper.map(hit, EndpointHitDto.class);

        return hitDto;
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start.isAfter(end)) {
            throw new BadRequestException(HttpStatus.BAD_REQUEST, "Тест на верную обработку запроса с неверными датами начала и конца диапазона времени");
        }

        PageRequest pageable = PageRequest.of(0, 20);
        if (unique) {
            return statsRepo.getUniqueViewStats(start, end, uris, pageable);
        } else {
            return statsRepo.getViewStats(start, end, uris, pageable);
        }
    }
}
