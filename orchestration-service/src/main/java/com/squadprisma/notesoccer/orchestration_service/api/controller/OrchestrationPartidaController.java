package com.squadprisma.notesoccer.orchestration_service.api.controller;

import com.squadprisma.notesoccer.orchestration_service.api.dto.CreatePartidaRequest;
import com.squadprisma.notesoccer.orchestration_service.api.dto.PartidaResponse;
import com.squadprisma.notesoccer.orchestration_service.application.service.MatchOrchestrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping(value = "/api/v1/orquestrador/partidas", produces = "application/json")
@RequiredArgsConstructor
@Tag(name = "Partidas", description = "Orchestrator → Match Service")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class OrchestrationPartidaController {

    private final MatchOrchestrationService service;

    @Operation(summary = "Criar Partida",
    description = "Encaminha a criação de partida para o match-service.",
    responses = {
            @ApiResponse(responseCode = "201", description = "Criado",
            content = @Content(schema = @Schema(implementation = PartidaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflito de horário/negócio", content = @Content)
    })
    @PostMapping(consumes = "application/json")
    public ResponseEntity<PartidaResponse> criar(@Valid @RequestBody CreatePartidaRequest req){
        log.info("Orchestrator - Criar partida. ligaId={}, mandanteId={}, visitanteId={}, dataHoraInicio={}",
                req.ligaId(), req.casaTimeId(), req.visitanteTimeId(), req.startAt());

        PartidaResponse resp = service.criar(req);
        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(resp.id())
                .toUri();

        log.info("Orchestrator - Partida criada com sucesso. partidaId={}, ligaId={}",
                resp.id(), resp.ligaId());

        return ResponseEntity.created(location).body(resp);
    }

    @Operation(summary = "Calendário por liga e intervalo (proxy para match-service)")
    @GetMapping("/calendario")
    public List<PartidaResponse> calendario(
            @RequestParam UUID ligaId,
            @RequestParam OffsetDateTime from,
            @RequestParam OffsetDateTime to) {
        log.info("Orchestrator - Consultar calendário de partidas. ligaId={}, from={}, to={}",
                ligaId, from, to);

        var resp = service.calendario(ligaId, from, to);

        log.debug("Orchestrator - Calendário retornado. ligaId={}, quantidadePartidas={}",
                ligaId, resp.size());

        return resp;
    }
}
