package com.squadprisma.notesoccer.orchestration_service.application.port.out;

import com.squadprisma.notesoccer.orchestration_service.api.dto.*;

import java.util.UUID;

public interface LeagueServicePort {
    LigaResponse criarLiga(CreateLigaRequest request);
    TimeResponse criarTime(CreateTimeRequest request);
    PageResponse<TimeResponse> listarTimes(UUID ligaId, int page, int size);
    TimeCountResponse contarTimes(UUID ligaId);
}
