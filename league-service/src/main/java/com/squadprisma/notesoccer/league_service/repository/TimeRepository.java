package com.squadprisma.notesoccer.league_service.repository;

import com.squadprisma.notesoccer.league_service.domain.entity.Liga;
import com.squadprisma.notesoccer.league_service.domain.entity.Time;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TimeRepository extends JpaRepository<Time, UUID> {

    boolean existsByLigaAndNomeIgnoreCase(Liga liga, String name);

    long countByLiga(Liga liga);

    Page<Time> findByLiga_IdOrderByNomeAsc(UUID ligaId, Pageable pageable);

    long deleteByLiga(Liga liga);
}
