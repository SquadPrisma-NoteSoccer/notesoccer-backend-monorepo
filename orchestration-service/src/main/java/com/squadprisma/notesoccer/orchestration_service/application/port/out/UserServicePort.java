package com.squadprisma.notesoccer.orchestration_service.application.port.out;

import com.squadprisma.notesoccer.orchestration_service.api.dto.CreateUsuarioRequest;
import com.squadprisma.notesoccer.orchestration_service.api.dto.UsuarioResponse;

public interface UserServicePort {
    UsuarioResponse criarUsuario(CreateUsuarioRequest request);
}
