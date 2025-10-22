package com.squadprisma.notesoccer.orchestration_service.application.service;

import com.squadprisma.notesoccer.orchestration_service.api.dto.CreateUsuarioRequest;
import com.squadprisma.notesoccer.orchestration_service.api.dto.UsuarioResponse;
import com.squadprisma.notesoccer.orchestration_service.application.port.out.UserServicePort;
import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserOrchestrationService {

    private final UserServicePort userPort;

    public UsuarioResponse criarUsuario(CreateUsuarioRequest req) {
        try {
            return userPort.criarUsuario(req);
        } catch (FeignException.Conflict e) {
            // 409 do user-service (e-mail duplicado, por ex.)
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.contentUTF8(), e);
        } catch (RetryableException e){
            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "user-service timeout", e);
        } catch (FeignException e) {
            // qualquer outro erro do user-service
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "user-service error: " + e.status(), e);
        }
    }
}
