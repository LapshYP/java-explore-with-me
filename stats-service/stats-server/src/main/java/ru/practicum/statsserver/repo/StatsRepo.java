package ru.practicum.statsserver.repo;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.statsdto.ViewStatsDto;
import ru.practicum.statsserver.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepo extends JpaRepository<EndpointHit, Long> {

    @Query(value = "SELECT new ru.practicum.statsdto.ViewStatsDto(endpointhit.app, endpointhit.uri, COUNT(DISTINCT endpointhit.ip)) " +
            "FROM EndpointHit endpointhit " +
            "WHERE endpointhit.timestamp BETWEEN :start AND :end " +
            "AND (endpointhit.uri IN :uris OR :uris is NULL) " +
            "GROUP BY endpointhit.app, endpointhit.uri " +
            "ORDER BY COUNT(endpointhit.ip) DESC ")
    List<ViewStatsDto> getUniqueViewStats(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                                          @Param("uris") List<String> uris, PageRequest pageable);

    @Query(value = "SELECT new ru.practicum.statsdto.ViewStatsDto(endpointhit.app, endpointhit.uri, COUNT(endpointhit.ip)) " +
            "FROM EndpointHit endpointhit " +
            "WHERE endpointhit.timestamp BETWEEN :start AND :end " +
            "AND (endpointhit.uri IN :uris OR :uris is NULL) " +
            "GROUP BY endpointhit.app, endpointhit.uri " +
            "ORDER BY COUNT(endpointhit.ip) DESC ")
    List<ViewStatsDto> getViewStats(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                                    @Param("uris") List<String> uris, PageRequest pageable);
}
