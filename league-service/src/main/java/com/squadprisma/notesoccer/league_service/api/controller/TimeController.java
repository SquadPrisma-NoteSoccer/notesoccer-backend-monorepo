package com.squadprisma.notesoccer.league_service.api.controller;

import com.squadprisma.notesoccer.league_service.api.dto.TimeCountResponse;
import com.squadprisma.notesoccer.league_service.api.dto.TimeCreateRequest;
import com.squadprisma.notesoccer.league_service.api.dto.TimeLoteCreateRequest;
import com.squadprisma.notesoccer.league_service.api.dto.TimeResponse;
import com.squadprisma.notesoccer.league_service.domain.entity.Time;
import com.squadprisma.notesoccer.league_service.service.TimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/times")
@RequiredArgsConstructor
@Tag(name = "Times", description = "Endpoints de times por liga")
public class TimeController {

    private final TimeService service;

    @PostMapping
    @Operation(summary = "Adiciona time à liga")
    public ResponseEntity<TimeResponse> create(@Valid @RequestBody TimeCreateRequest body) {
        Time t = service.create(body);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new TimeResponse(t.getId(), t.getLiga().getId(), t.getNome(), t.getCreatedAt()));
    }

    @PostMapping("/lote/{ligaId}")
    @Operation(summary = "Adiciona vários times à liga (lote)")
    public ResponseEntity<List<TimeResponse>> createLote(
            @PathVariable UUID ligaId,
            @Valid @RequestBody List<TimeLoteCreateRequest> body
    ) {
        List<Time> times = service.createLote(ligaId, body);

        List<TimeResponse> response = times.stream()
                .map(t -> new TimeResponse(
                        t.getId(),
                        t.getLiga().getId(),
                        t.getNome(),
                        t.getCreatedAt()
                )).toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar times de uma liga (ordem alfabética)")
    public Page<TimeResponse> list(
            @RequestParam UUID ligaId,
            @PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        return service.list(ligaId, pageable)
                .map(t -> new TimeResponse(t.getId(), t.getLiga().getId(), t.getNome(), t.getCreatedAt()));
    }

    @GetMapping("/count")
    @Operation(summary = "Contador de times por liga")
    public TimeCountResponse count(@RequestParam UUID ligaId) {
        return new TimeCountResponse(ligaId, service.count(ligaId));
    }
}
