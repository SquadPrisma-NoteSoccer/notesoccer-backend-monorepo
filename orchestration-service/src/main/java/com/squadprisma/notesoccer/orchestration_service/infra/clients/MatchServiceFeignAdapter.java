package com.squadprisma.notesoccer.orchestration_service.infra.clients;

import com.squadprisma.notesoccer.orchestration_service.api.dto.CreatePartidaRequest;
import com.squadprisma.notesoccer.orchestration_service.api.dto.PartidaResponse;
import com.squadprisma.notesoccer.orchestration_service.application.port.out.MatchServicePort;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MatchServiceFeignAdapter implements MatchServicePort {

    private final MatchServiceClient client;

    @Override
    public PartidaResponse criar(CreatePartidaRequest req) {
        try {
            return client.criar(req);
        } catch (FeignException ex) {
            throw mapFeign(ex, "Erro ao criar partida");
        }
    }

    @Override
    public List<PartidaResponse> calendario(UUID ligaId, OffsetDateTime from, OffsetDateTime to) {
        try {
            return client.calendario(ligaId, from, to);
        } catch (FeignException ex) {
            throw mapFeign(ex, "Erro ao consultar calendário");
        }
    }

    private ResponseStatusException mapFeign(FeignException ex, String message) {
        return switch (ex.status()) {
            case 400 -> new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
            case 404 -> new ResponseStatusException(HttpStatus.NOT_FOUND, message);
            case 409 -> new ResponseStatusException(HttpStatus.CONFLICT, message);
            case 401 -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, message);
            case 403 -> new ResponseStatusException(HttpStatus.FORBIDDEN, message);
            default -> new ResponseStatusException(HttpStatus.BAD_GATEWAY, message + " (" + ex.status() + ")");
        };
    }
}
