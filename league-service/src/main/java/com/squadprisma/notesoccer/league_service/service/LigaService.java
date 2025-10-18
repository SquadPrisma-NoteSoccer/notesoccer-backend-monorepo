package com.squadprisma.notesoccer.league_service.service;

import com.squadprisma.notesoccer.league_service.api.dto.LigaCreateRequest;
import com.squadprisma.notesoccer.league_service.domain.entity.Liga;
import com.squadprisma.notesoccer.league_service.repository.LigaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LigaService {

    private final LigaRepository repo;

    @Transactional
    public Liga create(LigaCreateRequest req) {
        Liga l = new Liga();
        l.setNome(req.nome().trim().replaceAll("\\s+"," "));
        return repo.save(l);
    }
}
