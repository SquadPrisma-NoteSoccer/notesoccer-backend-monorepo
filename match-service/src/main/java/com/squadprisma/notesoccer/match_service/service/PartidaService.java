package com.squadprisma.notesoccer.match_service.service;

import com.squadprisma.notesoccer.match_service.api.dto.PartidaRequest;
import com.squadprisma.notesoccer.match_service.api.dto.PartidaResponse;
import com.squadprisma.notesoccer.match_service.domain.entity.Partida;
import com.squadprisma.notesoccer.match_service.domain.enums.PartidaStatus;
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
    public PartidaResponse criar(PartidaRequest req){

        Partida p = Partida.builder()
                .ligaId(req.ligaId())
                .casaTimeId(req.casaTimeId())
                .visitanteTimeId(req.visitanteTimeId())
                .startAt(req.startAt())
                .endAt(req.endAt())
                .local(req.local())
                .notas(req.notas())
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .status(PartidaStatus.AGENDADA)
                .build();

        if (p.getCasaTimeId().equals(p.getVisitanteTimeId()))
            throw new IllegalArgumentException("Times devem ser distintos");

        if (!p.getStartAt().isBefore(p.getEndAt()))
            throw new IllegalArgumentException("Horário inválido");

        List<Partida> conflicts = repo.findConflicts(
                p.getLigaId(), p.getCasaTimeId(), p.getVisitanteTimeId(),
                p.getStartAt(), p.getEndAt());

        if (!conflicts.isEmpty())
            throw new IllegalStateException("Conflito de agenda");

        return toDto(repo.save(p));
    }

    public List<PartidaResponse> calendario(UUID ligaId, OffsetDateTime from, OffsetDateTime to){
        return repo.findByLigaIdAndStartAtBetween(ligaId, from, to).stream().map(partida -> toDto(partida)).toList();
    }

    private PartidaResponse toDto(Partida p){
        PartidaResponse response = new PartidaResponse(
                p.getId(), p.getLigaId(), p.getCasaTimeId(), p.getVisitanteTimeId(),
                p.getStartAt(), p.getEndAt(), p.getLocal(), p.getNotas(),
                p.getCreatedAt(), p.getUpdatedAt(), p.getStatus()
        );
        return response;
    }
}
