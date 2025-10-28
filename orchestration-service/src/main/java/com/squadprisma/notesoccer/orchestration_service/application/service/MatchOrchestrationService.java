package com.squadprisma.notesoccer.orchestration_service.application.service;

import com.squadprisma.notesoccer.orchestration_service.api.dto.CreatePartidaRequest;
import com.squadprisma.notesoccer.orchestration_service.api.dto.PartidaResponse;
import com.squadprisma.notesoccer.orchestration_service.application.port.out.MatchServicePort;
import com.squadprisma.notesoccer.orchestration_service.domain.exception.ConflictException;
import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MatchOrchestrationService {

    private final MatchServicePort matchPort;

    public PartidaResponse criar(CreatePartidaRequest req) {

        if (req.casaTimeId().equals(req.visitanteTimeId())) {
            throw new IllegalArgumentException("Times devem ser distintos");
        }
        if (!req.startAt().isBefore(req.endAt())) {
            throw new ConflictException("Horário inválido");
        }

        try {
            return matchPort.criar(req);
        } catch (FeignException.Conflict e) {
            // 409 do match-service (conflito de horário, times duplicados, etc.)
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.contentUTF8(), e);
        } catch (RetryableException e) {
            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "match-service timeout", e);
        } catch (FeignException e) {
            // qualquer outro erro retornado pelo match-service
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "match-service error: " + e.status(), e);
        }
    }

    public PartidaResponse buscarPorId(UUID id) {
        try {
            return matchPort.buscarPorId(id);
        } catch (FeignException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Partida não encontrada", e);
        } catch (RetryableException e) {
            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "match-service timeout", e);
        } catch (FeignException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "match-service error: " + e.status(), e);
        }
    }

    public List<PartidaResponse> listar(UUID ligaId, UUID teamId) {
        try {
            return matchPort.listar(ligaId, teamId);
        } catch (RetryableException e) {
            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "match-service timeout", e);
        } catch (FeignException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "match-service error: " + e.status(), e);
        }
    }

    public PartidaResponse alterarStatus(UUID id, String status) {
        try {
            return matchPort.alterarStatus(id, status);
        } catch (FeignException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Partida não encontrada", e);
        } catch (RetryableException e) {
            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "match-service timeout", e);
        } catch (FeignException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "match-service error: " + e.status(), e);
        }
    }
}
