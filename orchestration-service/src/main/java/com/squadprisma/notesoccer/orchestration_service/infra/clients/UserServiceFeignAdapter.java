package com.squadprisma.notesoccer.orchestration_service.infra.clients;

import com.squadprisma.notesoccer.orchestration_service.api.dto.CreateUsuarioRequest;
import com.squadprisma.notesoccer.orchestration_service.api.dto.UsuarioResponse;
import com.squadprisma.notesoccer.orchestration_service.application.port.out.UserServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserServiceFeignAdapter implements UserServicePort {

    private final UserServiceClient client;

    @Override
    public UsuarioResponse criarUsuario(CreateUsuarioRequest request) {
        return client.criar(request);
    }
}
