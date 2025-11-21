package com.squadprisma.notesoccer.orchestration_service.application.service;

import com.squadprisma.notesoccer.orchestration_service.api.dto.CreateUsuarioRequest;
import com.squadprisma.notesoccer.orchestration_service.api.dto.UsuarioResponse;
import com.squadprisma.notesoccer.orchestration_service.application.port.out.UserServicePort;
import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserOrchestrationService {

    private final UserServicePort userPort;

    public UsuarioResponse criarUsuario(CreateUsuarioRequest req) {
        log.info("[ORC-USER] Iniciando criação de usuário no user-service. email={}, apelido={}",
                req.email(), req.apelido());
        try {
            var created = userPort.criarUsuario(req);
            log.info("[ORC-USER] Usuário criado com sucesso no user-service. usuarioId={}, email={}",
                    created.id(), created.email());
            return created;
        } catch (FeignException.Conflict e) {
            // 409 do user-service (e-mail duplicado, por ex.)
            log.warn("[ORC-USER] Conflito ao criar usuário no user-service. email={}, detalhe={}",
                    req.email(), e.contentUTF8());
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.contentUTF8(), e);
        } catch (RetryableException e){
            log.error("[ORC-USER] Timeout ao chamar user-service na criação de usuário. email={}",
                    req.email(), e);
            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "user-service timeout", e);
        } catch (FeignException e) {
            log.error("[ORC-USER] Erro genérico do user-service na criação de usuário. email={}, status={}",
                    req.email(), e.status(), e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "user-service error: " + e.status(), e);
        }
    }
}
