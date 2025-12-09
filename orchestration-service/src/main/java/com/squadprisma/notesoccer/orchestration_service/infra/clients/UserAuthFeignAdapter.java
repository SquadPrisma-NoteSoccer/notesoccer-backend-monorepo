package com.squadprisma.notesoccer.orchestration_service.infra.clients;

import com.squadprisma.notesoccer.orchestration_service.api.dto.CadastroUsuarioRequest;
import com.squadprisma.notesoccer.orchestration_service.api.dto.LoginRequest;
import com.squadprisma.notesoccer.orchestration_service.api.dto.LoginResponse;
import com.squadprisma.notesoccer.orchestration_service.application.port.out.UserAuthPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAuthFeignAdapter implements UserAuthPort {

    private final UserAuthClient client;

    @Override
    public LoginResponse login(LoginRequest request) {
        return client.login(request);
    }

    @Override
    public LoginResponse signup(CadastroUsuarioRequest request) {
        return client.signup(request);
    }
}
