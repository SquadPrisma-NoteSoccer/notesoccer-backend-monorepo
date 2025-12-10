package com.squadprisma.notesoccer.league_service.service;

import com.squadprisma.notesoccer.league_service.api.dto.LigaCreateRequest;
import com.squadprisma.notesoccer.league_service.domain.entity.Liga;
import com.squadprisma.notesoccer.league_service.domain.exception.NotFoundException;
import com.squadprisma.notesoccer.league_service.repository.LigaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LigaService {

    private final LigaRepository repo;

    @Transactional
    public Liga criar(LigaCreateRequest req) {
        Liga l = new Liga();
        l.setNome(req.nome().trim().replaceAll("\\s+"," "));
        l.setUserId(req.userId());
        return repo.save(l);
    }

    public Page<Liga> listarPorUsuario(UUID userId, Pageable pageable){
        return repo.findByUserId(userId, pageable);

    }

    @Transactional
    public void delete(UUID ligaId){
        boolean exists = repo.existsById(ligaId);

        if (!exists){
            throw new NotFoundException("LEAGUE_NOT_FOUND");
        }

        repo.deleteByLigaId(ligaId);
    }
}
