package com.squadprisma.notesoccer.league_service.service;

import com.squadprisma.notesoccer.league_service.api.dto.TimeCreateRequest;
import com.squadprisma.notesoccer.league_service.api.dto.TimeLoteCreateRequest;
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


import java.util.*;
import java.util.stream.Collectors;

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

    @Transactional
    public List<Time> createLote(UUID ligaId, List<TimeLoteCreateRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new BadRequestException("TEAM_LIST_REQUIRED");
        }

        Liga liga = ligaRepo.findById(ligaId)
                .orElseThrow(() -> new NotFoundException("LEAGUE_NOT_FOUND"));

        long qtdAtual = timeRepo.countByLiga(liga);
        if (qtdAtual >= TEAM_LIMIT_PER_LEAGUE) {
            throw new ConflictException("TEAM_LIMIT_REACHED");
        }

        // Normaliza nomes, remove brancos e duplicados (case-insensitive)
        List<String> nomesNormalizados = new ArrayList<>();
        Set<String> vistosLower = new HashSet<>();

        for (TimeLoteCreateRequest req : requests) {
            String nome = req.nome().trim().replaceAll("\\s+"," ");
            if (nome.isBlank()) {
                throw new BadRequestException("TEAM_NAME_REQUIRED");
            }

            String lower = nome.toLowerCase(Locale.ROOT);
            if (vistosLower.contains(lower)) {
                // se vier repetido no próprio lote, ignora o duplicado
                continue;
            }
            vistosLower.add(lower);

            // se já existir na liga, mantém mesmo comportamento do create()
            if (timeRepo.existsByLigaAndNomeIgnoreCase(liga, nome)) {
                throw new ConflictException("TEAM_ALREADY_EXISTS");
            }

            nomesNormalizados.add(nome);
        }

        // Checa limite total (atuais + novos) antes de persistir
        if (qtdAtual + nomesNormalizados.size() > TEAM_LIMIT_PER_LEAGUE) {
            throw new ConflictException("TEAM_LIMIT_REACHED");
        }

        List<Time> times = nomesNormalizados.stream()
                .map(nome -> {
                    Time t = new Time();
                    t.setLiga(liga);
                    t.setNome(nome);
                    return t;
                })
                .collect(Collectors.toList());

        return timeRepo.saveAll(times);
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
