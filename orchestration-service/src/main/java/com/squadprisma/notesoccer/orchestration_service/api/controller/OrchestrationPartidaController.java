package com.squadprisma.notesoccer.orchestration_service.api.controller;

import com.squadprisma.notesoccer.orchestration_service.api.dto.CreatePartidaRequest;
import com.squadprisma.notesoccer.orchestration_service.api.dto.PartidaResponse;
import com.squadprisma.notesoccer.orchestration_service.application.service.MatchOrchestrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping(value = "/api/v1/orquestrador/partidas",
        produces = "application/json")
@RequiredArgsConstructor
@Tag(name = "Partidas", description = "Orchestrator → Match Service")
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
        PartidaResponse resp = service.criar(req);
        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(resp.id())
                .toUri();
        return ResponseEntity.created(location).body(resp);
    }

    @Operation(
            summary = "Buscar partida por ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = PartidaResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Não encontrada", content = @Content)
            }
    )
    @GetMapping("/{id}")
    public PartidaResponse buscarPorId(
            @Parameter(description = "ID da partida", required = true)
            @PathVariable("id") UUID id) {
        return service.buscarPorId(id);
    }

    @Operation(
            summary = "Listar partidas",
            description = "Lista partidas por filtros opcionais (ligaId e/ou teamId)."
    )
    @GetMapping
    public List<PartidaResponse> listar(
            @Parameter(description = "ID da liga") @RequestParam(value = "ligaId", required = false) UUID ligaId,
            @Parameter(description = "ID do time (pode retornar partidas como casa ou visitante)")
            @RequestParam(value = "timeId", required = false) UUID timeId) {
        return service.listar(ligaId, timeId);
    }

    @Operation(
            summary = "Alterar status da partida",
            description = "Encaminha a troca de status para o match-service (ex.: AGENDADA → ATUALIZADA → CANCELADA → FINALIZADA)."
    )
    @PatchMapping("/{id}/status")
    public PartidaResponse alterarStatus(
            @Parameter(description = "ID da partida", required = true)
            @PathVariable("id") UUID id,
            @Parameter(description = "Novo status", required = true, example = "AGENDADA")
            @RequestParam("status") String status) {
        return service.alterarStatus(id, status);
    }
}
