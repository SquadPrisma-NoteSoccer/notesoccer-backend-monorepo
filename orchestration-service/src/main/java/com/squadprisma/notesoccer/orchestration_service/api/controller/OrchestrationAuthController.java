package com.squadprisma.notesoccer.orchestration_service.api.controller;

import com.squadprisma.notesoccer.orchestration_service.api.dto.CadastroUsuarioRequest;
import com.squadprisma.notesoccer.orchestration_service.api.dto.LoginRequest;
import com.squadprisma.notesoccer.orchestration_service.api.dto.LoginResponse;
import com.squadprisma.notesoccer.orchestration_service.infra.clients.UserAuthClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "Orchestrator → User Service Auth")
@RestController
@RequestMapping("/api/v1/auth")
public class OrchestrationAuthController {

    private final UserAuthClient userAuthClient;

    public OrchestrationAuthController(UserAuthClient userAuthClient) {
        this.userAuthClient = userAuthClient;
    }

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
        LoginResponse response = userAuthClient.login(request);
        return ResponseEntity.ok(response);
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
        LoginResponse response = userAuthClient.signup(request);
        return ResponseEntity.status(201).body(response);
    }
}
