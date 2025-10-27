package com.squadprisma.notesoccer.match_service.api.controller;

import com.squadprisma.notesoccer.match_service.api.dto.PartidaRequest;
import com.squadprisma.notesoccer.match_service.api.dto.PartidaResponse;
import com.squadprisma.notesoccer.match_service.domain.entity.Partida;
import com.squadprisma.notesoccer.match_service.domain.enums.PartidaStatus;
import com.squadprisma.notesoccer.match_service.service.PartidaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/partidas")
@RequiredArgsConstructor
@Tag(name = "Partidas")
public class PartidaController {

    private final PartidaService service;

    @PostMapping
    @Operation(summary = "Criar uma Partida")
    public PartidaResponse criar(@RequestBody @Valid PartidaRequest req){
        Partida p = Partida.builder()
                .ligaId(req.ligaId())
                .casaTimeId(req.casaTimeId())
                .visitanteTimeId((req.visitanteTimeId()))
                .startAt(req.startAt())
                .endAt(req.endAt())
                .local(req.local())
                .notas(req.notas())
                .status(PartidaStatus.AGENDADA)
                .build();
        return toDto(service.criar(p));
    }

    @GetMapping("/calendario")
    @Operation(summary = "Consultar as partidas de uma liga entre um intervalo de datas")
    public List<PartidaResponse> calendario(@RequestParam UUID ligaId,
                                            @RequestParam OffsetDateTime from,
                                            @RequestParam OffsetDateTime to){
        return service.calendario(ligaId, from, to).stream().map(this::toDto).toList();
    }

    private PartidaResponse toDto(Partida p){
        return new PartidaResponse(
                p.getId(), p.getLigaId(), p.getCasaTimeId(), p.getVisitanteTimeId(),
                p.getStartAt(), p.getEndAt(), p.getLocal(), p.getNotas(), p.getStatus()
        );
    }
}
