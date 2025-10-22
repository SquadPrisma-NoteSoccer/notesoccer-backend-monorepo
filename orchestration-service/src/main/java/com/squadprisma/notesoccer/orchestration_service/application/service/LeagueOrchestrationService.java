package com.squadprisma.notesoccer.orchestration_service.application.service;

import com.squadprisma.notesoccer.orchestration_service.api.dto.*;
import com.squadprisma.notesoccer.orchestration_service.application.port.out.LeagueServicePort;
import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

/**
 * Orquestra as chamadas para o league-service.
 * Faz o mapeamento de exceções Feign para ResponseStatusException, de forma
 * padronizada e consistente com o UserOrchestrationService.
 */
@Service
@RequiredArgsConstructor
public class LeagueOrchestrationService {

    private final LeagueServicePort port;

    public LigaResponse criarLiga(CreateLigaRequest req) {
        try {
            return port.criarLiga(req);
        } catch (FeignException.Conflict ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.contentUTF8());
        } catch (RetryableException ex) {
            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "league-service timeout");
        } catch (FeignException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "league-service error: " + ex.status());
        }
    }

    public TimeResponse criarTime(UUID ligaId, String nome) {
        try {
            var req = new CreateTimeRequest(ligaId, nome);
            return port.criarTime(req);
        } catch (FeignException.Conflict ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.contentUTF8());
        } catch (RetryableException ex) {
            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "league-service timeout");
        } catch (FeignException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "league-service error: " + ex.status());
        }
    }

    public PageResponse<TimeResponse> listarTimes(UUID ligaId, int page, int size) {
        // listagem normalmente não precisa de tratamento Feign específico
        return port.listarTimes(ligaId, page, size);
    }

    public TimeCountResponse contarTimes(UUID ligaId) {
        return port.contarTimes(ligaId);
    }
}
