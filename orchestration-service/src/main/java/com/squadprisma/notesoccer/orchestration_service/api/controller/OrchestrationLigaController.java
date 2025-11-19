package com.squadprisma.notesoccer.orchestration_service.api.controller;

import com.squadprisma.notesoccer.orchestration_service.api.dto.*;
import com.squadprisma.notesoccer.orchestration_service.application.service.LeagueOrchestrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Ligas")
@RestController
@RequestMapping("/api/v1/orquestrador/ligas")
@RequiredArgsConstructor
public class OrchestrationLigaController {

    private final LeagueOrchestrationService service;

    @PostMapping
    @Operation(summary = "Criar liga")
    public ResponseEntity<LigaResponse> criarLiga(@Valid @RequestBody CreateLigaRequest body) {
        var resp = service.criarLiga(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PostMapping("/{ligaId}/times")
    @Operation(summary = "Criar time em uma liga")
    public ResponseEntity<TimeResponse> criarTime(
            @PathVariable UUID ligaId,
            @Valid @RequestBody CreateTimeRequest body) {
        var resp = service.criarTime(ligaId, body.nome()); // força o path como fonte da liga
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PostMapping("/{ligaId}/times/lote")
    @Operation(summary = "Criar vários times em uma liga (lote)")
    public ResponseEntity<List<TimeResponse>> criarTimesLote(
            @PathVariable UUID ligaId,
            @Valid @RequestBody List<CreateTimeLoteRequest> body) {

        var resp = service.criarTimesLote(ligaId, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @GetMapping("/{ligaId}/times")
    @Operation(summary = "Listar times por liga")
    public PageResponse<TimeResponse> listarTimes(
            @PathVariable UUID ligaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.listarTimes(ligaId, page, size);
    }

    @GetMapping("/{ligaId}/times/count")
    @Operation(summary = "Contar times por liga")
    public TimeCountResponse contarTimes(@PathVariable UUID ligaId) {
        return service.contarTimes(ligaId);
    }
}
