package com.squadprisma.notesoccer.user_service.domain.service;

import com.squadprisma.notesoccer.user_service.api.dto.LoginRequest;
import com.squadprisma.notesoccer.user_service.api.dto.LoginResponse;
import com.squadprisma.notesoccer.user_service.config.UsuarioDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {

        var authToken = new UsernamePasswordAuthenticationToken(
                request.email(),
                request.senha()
        );

        var authentication = authenticationManager.authenticate(authToken);

        var usuarioDetails = (UsuarioDetails) authentication.getPrincipal();
        var usuario = usuarioDetails.getUsuario();

        String token = jwtService.generateToken(usuario);

        return new LoginResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole().name(),
                token
        );
    }
}
