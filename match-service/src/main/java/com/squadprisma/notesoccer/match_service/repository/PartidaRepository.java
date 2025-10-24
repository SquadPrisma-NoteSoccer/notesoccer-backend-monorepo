package com.squadprisma.notesoccer.match_service.repository;

import com.squadprisma.notesoccer.match_service.domain.entity.Partida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface PartidaRepository extends JpaRepository<Partida, UUID> {

    @Query("""
    select p from Partida p
     where p.ligaId = :ligaId
       and (p.casaTimeId in (:casaTimeId, :visitanteTimeId) or p.visitanteTimeId in (:casaTimeId, :visitanteTimeId))
       and p.startAt < :endAt and p.endAt > :startAt
  """)
    List<Partida> findConflicts(UUID ligaId, UUID casaTimeId, UUID visitanteTimeId,
                                OffsetDateTime startAt, OffsetDateTime endAt);

    List<Partida> findByLigaIdAndStartAtBetween(UUID ligaId, OffsetDateTime from, OffsetDateTime to);
}
