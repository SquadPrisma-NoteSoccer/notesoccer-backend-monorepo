package com.squadprisma.notesoccer.match_service.service;

import com.squadprisma.notesoccer.match_service.domain.entity.Partida;
import com.squadprisma.notesoccer.match_service.domain.exception.ConflictException;
import com.squadprisma.notesoccer.match_service.repository.PartidaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PartidaService {

    private final PartidaRepository repo;

    @Transactional
    public Partida criar(Partida p){
        if (p.getCasaTimeId().equals(p.getVisitanteTimeId()))
            throw new IllegalArgumentException("Times devem ser distintos");

        if (!p.getStartAt().isBefore(p.getEndAt()))
            throw new IllegalArgumentException("Horário inválido");

        List<Partida> conflicts = repo.findConflicts(
                p.getLigaId(), p.getCasaTimeId(), p.getVisitanteTimeId(), p.getStartAt(), p.getEndAt());

        if (!conflicts.isEmpty())
            throw new IllegalStateException("Conflito de agenda");

        return repo.save(p);
    }

    public List<Partida> calendario(UUID ligaId, OffsetDateTime from, OffsetDateTime to){
        return repo.findByLigaIdAndStartAtBetween(ligaId, from, to);
    }
}
