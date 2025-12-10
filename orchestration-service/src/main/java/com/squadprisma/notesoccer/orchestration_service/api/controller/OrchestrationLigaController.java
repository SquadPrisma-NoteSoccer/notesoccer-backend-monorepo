package com.squadprisma.notesoccer.orchestration_service.api.controller;

import com.squadprisma.notesoccer.orchestration_service.api.dto.*;
import com.squadprisma.notesoccer.orchestration_service.application.service.LeagueOrchestrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Ligas", description = "Orchestrator → League Service")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/orquestrador/ligas")
@RequiredArgsConstructor
@Slf4j
public class OrchestrationLigaController {

    private final LeagueOrchestrationService service;

    @PostMapping
    @Operation(summary = "Criar liga")
    public ResponseEntity<LigaResponse> criarLiga(@Valid @RequestBody CreateLigaRequest body) {
        log.info("Orchestrator - Recebida requisição para criar liga. nome={}", body.nome());

        var resp = service.criarLiga(body);

        log.info("Orchestrator - Liga criada com sucesso. ligaId={}", resp.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PostMapping("/{ligaId}/times")
    @Operation(summary = "Criar time em uma liga")
    public ResponseEntity<TimeResponse> criarTime(
            @PathVariable UUID ligaId,
            @Valid @RequestBody CreateTimeRequest body) {

        log.info("Orchestrator - Criar time em liga. ligaId={}, nomeTime={}", ligaId, body.nome());

        var resp = service.criarTime(ligaId, body.nome()); // força o path como fonte da liga

        log.info("Orchestrator - Time criado em liga. ligaId={}, timeId={}", ligaId, resp.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PostMapping("/{ligaId}/times/lote")
    @Operation(summary = "Criar vários times em uma liga (lote)")
    public ResponseEntity<List<TimeResponse>> criarTimesLote(
            @PathVariable UUID ligaId,
            @Valid @RequestBody List<CreateTimeLoteRequest> body) {

        log.info("Orchestrator - Criar times em lote. ligaId={}, quantidadeTimes={}", ligaId, body.size());

        var resp = service.criarTimesLote(ligaId, body);

        log.info("Orchestrator - Lote de times criado. ligaId={}, quantidadeCriada={}", ligaId, resp.size());
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @GetMapping("/{ligaId}/times")
    @Operation(summary = "Listar times por liga")
    public PageResponse<TimeResponse> listarTimes(
            @PathVariable UUID ligaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("Orchestrator - Listar times. ligaId={}, page={}, size={}", ligaId, page, size);

        var resp = service.listarTimes(ligaId, page, size);

        log.debug("Orchestrator - Listagem de times concluída. ligaId={}, totalElements={}",
                ligaId, resp.totalElements());

        return resp;
    }

    @GetMapping("/{ligaId}/times/count")
    @Operation(summary = "Contar times por liga")
    public TimeCountResponse contarTimes(@PathVariable UUID ligaId) {
        log.info("Orchestrator - Contar times da liga. ligaId={}", ligaId);

        var resp = service.contarTimes(ligaId);

        log.info("Orchestrator - Contagem de times concluída. ligaId={}, totalTimes={}",
                ligaId, resp.count());

        return resp;
    }

    @DeleteMapping("/{ligaId}/times/{timeId}")
    @Operation(summary = "Excluir time de uma liga")
    public ResponseEntity<Void> deletarTime(
            @PathVariable UUID ligaId,
            @PathVariable UUID timeId
    ) {
        log.info("Orchestrator - Excluir time de liga. ligaId={}, timeId={}", ligaId, timeId);

        service.deletarTime(ligaId, timeId);

        log.info("Orchestrator - Time excluído com sucesso. ligaId={}, timeId={}", ligaId, timeId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{ligaId}")
    @Operation(summary = "Excluir liga")
    public ResponseEntity<Void> deletarLiga(@PathVariable UUID ligaId) {
        log.info("Orchestrator - Excluir liga. ligaId={}", ligaId);

        service.deletarLiga(ligaId);

        log.info("Orchestrator - Liga excluída com sucesso. ligaId={}", ligaId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Listar ligas de um usuário")
    public PageResponse<LigaResponse> listarLigasPorUsuario(
            @RequestParam UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("Orchestrator - Listar ligas por usuário. userId={}, page={}, size={}", userId, page, size);

        var resp = service.listarLigasPorUsuario(userId, page, size);

        log.debug("Orchestrator - Listagem de ligas concluída. userId={}, totalElements={}",
                userId, resp.totalElements());

        return resp;
    }
}
