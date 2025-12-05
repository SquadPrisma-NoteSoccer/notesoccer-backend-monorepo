package com.squadprisma.notesoccer.orchestration_service.config;

import com.squadprisma.notesoccer.orchestration_service.api.dto.CadastroUsuarioRequest;
import com.squadprisma.notesoccer.orchestration_service.api.dto.LoginRequest;
import com.squadprisma.notesoccer.orchestration_service.api.dto.LoginResponse;
import com.squadprisma.notesoccer.orchestration_service.infra.clients.UserAuthClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!test")
public class AuthOrchestrationService {

    private final UserAuthClient userAuthClient;

    public AuthOrchestrationService(UserAuthClient userAuthClient) {
        this.userAuthClient = userAuthClient;
    }

    public LoginResponse login(LoginRequest request) {
        return userAuthClient.login(request);
    }

    public LoginResponse signup(CadastroUsuarioRequest request) {
        return userAuthClient.signup(request);
    }
}
