package com.squadprisma.notesoccer.orchestration_service.application.service;

import com.squadprisma.notesoccer.orchestration_service.api.dto.CadastroUsuarioRequest;
import com.squadprisma.notesoccer.orchestration_service.api.dto.LoginRequest;
import com.squadprisma.notesoccer.orchestration_service.api.dto.LoginResponse;
import com.squadprisma.notesoccer.orchestration_service.application.port.out.UserAuthPort;
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
public class UserAuthOrchestrationService {

    private final UserAuthPort userAuthPort;

    public LoginResponse login(LoginRequest request){
        log.info("[ORC-USER_AUTH] Iniciando LOGIN do usuário no user-service. email={}",
                request.email());
        try {
            var loggedIn = userAuthPort.login(request);
            log.info("[ORC-USER_AUTH] Usuário LOGADO com sucesso. usuarioId={}, email={}",
                    loggedIn.userId(), loggedIn.email());
            return loggedIn;
        } catch (FeignException.Forbidden e) {
            log.warn("[ORC-USER_AUTH] E-mail ou senha inválidos. email={}, detalhe={}",
                    request.email(), e.contentUTF8());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.contentUTF8(), e);
        } catch (RetryableException e) {
            log.error("[ORC-USER_AUTH] Timeout ao chamar user-service no login. email={}",
                    request.email(), e);
            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "user-service timeout", e);
        } catch (FeignException e) {
            log.error("[ORC-USER_AUTH] Erro genérico do user-service no login. email={}, status={}",
                    request.email(), e.status(), e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "user-service error: " + e.status(), e);
        }
    }

    public LoginResponse signup(CadastroUsuarioRequest request){
        log.info("[ORC-USER_AUTH] Iniciando cadastro do usuário com autenticação no user-service. email={}",
                request.email());
        try{
            var created = userAuthPort.signup(request);
            log.info("[ORC-USER_AUTH] Usuário autenticado criado com sucesso no user-service. usuarioId={}, email={}",
                    created.userId(), created.email());
            return created;
        } catch (FeignException.Conflict e) {
            // 409 do user-service (e-mail duplicado, por ex.)
            log.warn("[ORC-USER_AUTH] Conflito ao criar usuário no user-service. email={}, detalhe={}",
                    request.email(), e.contentUTF8());
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.contentUTF8(), e);
        } catch (RetryableException e){
            log.error("[ORC-USER_AUTH] Timeout ao chamar user-service na criação de usuário. email={}",
                    request.email(), e);
            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "user-service timeout", e);
        } catch (FeignException e) {
            log.error("[ORC-USER_AUTH] Erro genérico do user-service na criação de usuário. email={}, status={}",
                    request.email(), e.status(), e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "user-service error: " + e.status(), e);
        }
    }
}
