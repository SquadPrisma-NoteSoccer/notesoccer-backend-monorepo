package com.squadprisma.notesoccer.orchestration_service.application.port.out;

import com.squadprisma.notesoccer.orchestration_service.api.dto.CreatePartidaRequest;
import com.squadprisma.notesoccer.orchestration_service.api.dto.PartidaResponse;

import java.util.List;
import java.util.UUID;

public interface MatchServicePort {
    PartidaResponse criar(CreatePartidaRequest req);
    PartidaResponse buscarPorId(UUID id);
    List<PartidaResponse> listar(UUID ligaId, UUID timeId);
    PartidaResponse alterarStatus(UUID id, String status);
}
