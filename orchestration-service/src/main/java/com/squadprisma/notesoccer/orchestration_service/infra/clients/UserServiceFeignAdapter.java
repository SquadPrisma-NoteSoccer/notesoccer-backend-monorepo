package com.squadprisma.notesoccer.orchestration_service.infra.clients;

import com.squadprisma.notesoccer.orchestration_service.api.dto.CreateUsuarioRequest;
import com.squadprisma.notesoccer.orchestration_service.api.dto.UsuarioResponse;
import com.squadprisma.notesoccer.orchestration_service.application.port.out.UserServicePort;
import org.springframework.stereotype.Component;

@Component
public class UserServiceFeignAdapter implements UserServicePort {

    private final UserServiceClient client;

    public UserServiceFeignAdapter(UserServiceClient client){
        this.client = client;
    }

    @Override
    public UsuarioResponse criarUsuario(CreateUsuarioRequest request) {
        return client.criar(request);
    }
}
