package com.squadprisma.notesoccer.orchestration_service.application.port.out;

import com.squadprisma.notesoccer.orchestration_service.api.dto.CadastroUsuarioRequest;
import com.squadprisma.notesoccer.orchestration_service.api.dto.LoginRequest;
import com.squadprisma.notesoccer.orchestration_service.api.dto.LoginResponse;

public interface UserAuthPort {
    LoginResponse login(LoginRequest request);
    LoginResponse signup(CadastroUsuarioRequest request);
}
