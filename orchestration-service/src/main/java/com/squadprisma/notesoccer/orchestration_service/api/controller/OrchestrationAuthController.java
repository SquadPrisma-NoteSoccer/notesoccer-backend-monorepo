package com.squadprisma.notesoccer.orchestration_service.api.controller;

import com.squadprisma.notesoccer.orchestration_service.api.dto.CadastroUsuarioRequest;
import com.squadprisma.notesoccer.orchestration_service.api.dto.LoginRequest;
import com.squadprisma.notesoccer.orchestration_service.api.dto.LoginResponse;
import com.squadprisma.notesoccer.orchestration_service.application.service.UserAuthOrchestrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "Orchestrator → User Service Auth")
@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
@RequiredArgsConstructor
public class OrchestrationAuthController {

    private final UserAuthOrchestrationService service;

    @Operation(
            summary = "Login via gateway",
            description = "Recebe email e senha do front e repassa para o user-service. Retorna token JWT."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login OK",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Erro de validação"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        log.info("Orchestrator - Efetuar Login do Usuário email={}", request.email());
        var loggedIn = service.login(request);
        log.info("Orchestrator - Login efetuado com sucesso. usuarioId={}, email={}",
                loggedIn.userId(), loggedIn.email());
        return ResponseEntity.ok(loggedIn);
    }

    @Operation(
            summary = "Cadastro via gateway",
            description = "Recebe os dados do usuário do front, repassa para o user-service e retorna token JWT."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Erro de validação"),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado")
    })
    @PostMapping("/signup")
    public ResponseEntity<LoginResponse> signup(@RequestBody @Valid CadastroUsuarioRequest request) {
        log.info("Orchestrator - Criar usuário autenticado. email={}, apelido={}", request.email(), request.apelido());
        var created = service.signup(request);
        log.info("Orchestrator - Usuário criado com sucesso. usuarioId={}, email={}",
                created.userId(), created.email());
        return ResponseEntity.status(201).body(created);
    }
}
