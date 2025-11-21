package com.squadprisma.notesoccer.orchestration_service.api.controller;

import com.squadprisma.notesoccer.orchestration_service.api.dto.CreateUsuarioRequest;
import com.squadprisma.notesoccer.orchestration_service.api.dto.UsuarioResponse;
import com.squadprisma.notesoccer.orchestration_service.application.service.UserOrchestrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Usuarios")
@RestController
@RequestMapping("/api/v1/orquestrador/usuarios")
@RequiredArgsConstructor
@Slf4j
public class OrchestrationUsuarioController {

    private final UserOrchestrationService service;

    public OrchestrationUsuarioController(UserOrchestrationService service) {
        this.service = service;
    }

    @Operation(summary = "Cadastra um usuário")
    @PostMapping
    public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody CreateUsuarioRequest req) {
        log.info("Orchestrator - Criar usuário. email={}, apelido={}", req.email(), req.apelido());

        var created = service.criarUsuario(req);

        log.info("Orchestrator - Usuário criado com sucesso. usuarioId={}, email={}",
                created.id(), created.email());

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
