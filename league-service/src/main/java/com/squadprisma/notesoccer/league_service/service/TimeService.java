package com.squadprisma.notesoccer.league_service.service;

import com.squadprisma.notesoccer.league_service.api.dto.TimeCreateRequest;
import com.squadprisma.notesoccer.league_service.domain.entity.Liga;
import com.squadprisma.notesoccer.league_service.domain.entity.Time;
import com.squadprisma.notesoccer.league_service.domain.exception.BadRequestException;
import com.squadprisma.notesoccer.league_service.domain.exception.ConflictException;
import com.squadprisma.notesoccer.league_service.domain.exception.NotFoundException;
import com.squadprisma.notesoccer.league_service.repository.LigaRepository;
import com.squadprisma.notesoccer.league_service.repository.TimeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TimeService {

    public static final int TEAM_LIMIT_PER_LEAGUE = 20;

    private final TimeRepository timeRepo;
    private final LigaRepository ligaRepo;

    @Transactional
    public Time create(TimeCreateRequest req) {
        String nome = req.nome().trim().replaceAll("\\s+"," ");
        if (nome.isBlank()) throw new BadRequestException("TEAM_NAME_REQUIRED");

        Liga liga = ligaRepo.findById(req.ligaId())
                .orElseThrow(() -> new NotFoundException("LEAGUE_NOT_FOUND"));

        long qtd = timeRepo.countByLiga(liga);
        if (qtd >= TEAM_LIMIT_PER_LEAGUE) throw new ConflictException("TEAM_LIMIT_REACHED");

        if (timeRepo.existsByLigaAndNomeIgnoreCase(liga, nome))
            throw new ConflictException("TEAM_ALREADY_EXISTS");

        Time t = new Time();
        t.setLiga(liga);
        t.setNome(nome);
        return timeRepo.save(t);
    }

    public Page<Time> list(UUID ligaId, Pageable pageable) {
        return timeRepo.findByLiga_IdOrderByNomeAsc(ligaId, pageable);
    }

    public long count(UUID ligaId) {
        Liga liga = ligaRepo.findById(ligaId)
                .orElseThrow(() -> new NotFoundException("LEAGUE_NOT_FOUND"));
        return timeRepo.countByLiga(liga);
    }
}
