package com.squadprisma.notesoccer.user_service.api.controller;

import com.squadprisma.notesoccer.user_service.api.dto.CadastroUsuarioDTO;
import com.squadprisma.notesoccer.user_service.api.dto.LoginRequest;
import com.squadprisma.notesoccer.user_service.api.dto.LoginResponse;
import com.squadprisma.notesoccer.user_service.security.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<LoginResponse> signup(@Valid @RequestBody CadastroUsuarioDTO dto) {
        LoginResponse response = authService.signup(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
