package com.squadprisma.notesoccer.league_service.api.controller;

import com.squadprisma.notesoccer.league_service.api.dto.LigaCreateRequest;
import com.squadprisma.notesoccer.league_service.api.dto.LigaResponse;
import com.squadprisma.notesoccer.league_service.domain.entity.Liga;
import com.squadprisma.notesoccer.league_service.service.LigaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ligas")
@RequiredArgsConstructor
@Tag(name = "Ligas", description = "Endpoints de cadastro e listagem de ligas")
public class LigaController {

    private final LigaService service;

    @PostMapping
    @Operation(summary = "Criar uma nova liga")
    public ResponseEntity<LigaResponse> criar(@Valid @RequestBody LigaCreateRequest body) {
        Liga l = service.criar(body);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new LigaResponse(l.getId(), l.getNome(), l.getUserId(), l.getCreatedAt()));
    }

    @Operation(summary = "Listar ligas por usuário")
    @GetMapping
    public Page<LigaResponse> listarPorUsuario(
            @RequestParam UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ){
        var p = service.listarPorUsuario(userId, PageRequest.of(page, size))
                .map(LigaController::toResponse);
        return new PageImpl<>(p.getContent(), p.getPageable(), p.getTotalElements());
    }

    @DeleteMapping("/{ligaId}")
    @Operation(summary = "Excluir liga")
    public ResponseEntity<Void> delete(@PathVariable UUID ligaId) {
        service.delete(ligaId);
        return ResponseEntity.noContent().build();
    }

     private static LigaResponse toResponse(Liga l) {
        return new LigaResponse(l.getId(), l.getNome(), l.getUserId(), l.getCreatedAt());
    }


}
