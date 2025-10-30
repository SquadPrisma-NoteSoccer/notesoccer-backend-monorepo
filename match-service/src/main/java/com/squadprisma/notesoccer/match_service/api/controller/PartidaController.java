package com.squadprisma.notesoccer.match_service.api.controller;

import com.squadprisma.notesoccer.match_service.api.dto.PartidaRequest;
import com.squadprisma.notesoccer.match_service.api.dto.PartidaResponse;
import com.squadprisma.notesoccer.match_service.domain.entity.Partida;
import com.squadprisma.notesoccer.match_service.service.PartidaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<PartidaResponse> criar(@RequestBody @Valid PartidaRequest req){
        PartidaResponse p = service.criar(req);
        return ResponseEntity.ok(p);
    }

    @GetMapping("/calendario")
    @Operation(summary = "Consultar as partidas de uma liga entre um intervalo de datas")
    public List<PartidaResponse> calendario(@RequestParam UUID ligaId,
                                            @RequestParam OffsetDateTime from,
                                            @RequestParam OffsetDateTime to){
        return service.calendario(ligaId, from, to).stream().toList();
    }
    }