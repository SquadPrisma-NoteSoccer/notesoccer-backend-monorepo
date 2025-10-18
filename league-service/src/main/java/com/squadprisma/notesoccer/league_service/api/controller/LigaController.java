package com.squadprisma.notesoccer.league_service.api.controller;

import com.squadprisma.notesoccer.league_service.api.dto.LigaCreateRequest;
import com.squadprisma.notesoccer.league_service.api.dto.LigaResponse;
import com.squadprisma.notesoccer.league_service.domain.entity.Liga;
import com.squadprisma.notesoccer.league_service.service.LigaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/ligas")
@RequiredArgsConstructor
@Tag(name = "Ligas", description = "Endpoints de cadastro e listagem de ligas")
public class LigaController {

    private final LigaService service;

    @PostMapping
    @Operation(summary = "Criar uma nova liga")
    public ResponseEntity<LigaResponse> create(@Valid @RequestBody LigaCreateRequest body) {
        Liga l = service.create(body);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new LigaResponse(l.getId(), l.getNome(), l.getCreatedAt()));
    }
}
