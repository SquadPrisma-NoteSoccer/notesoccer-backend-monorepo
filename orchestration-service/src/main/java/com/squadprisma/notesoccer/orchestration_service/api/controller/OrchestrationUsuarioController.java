package com.squadprisma.notesoccer.orchestration_service.api.controller;

import com.squadprisma.notesoccer.orchestration_service.api.dto.CreateUsuarioRequest;
import com.squadprisma.notesoccer.orchestration_service.api.dto.UsuarioResponse;
import com.squadprisma.notesoccer.orchestration_service.application.service.UserOrchestrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Usuarios")
@RestController
@RequestMapping("api/v1/orchestrador-usuarios")
public class OrchestrationUsuarioController {

    private final UserOrchestrationService service;

    public OrchestrationUsuarioController(UserOrchestrationService service) {
        this.service = service;
    }

    @Operation(summary = "Cadastra um usuário")
    @PostMapping
    public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody CreateUsuarioRequest req) {
        var created = service.criarUsuario(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
