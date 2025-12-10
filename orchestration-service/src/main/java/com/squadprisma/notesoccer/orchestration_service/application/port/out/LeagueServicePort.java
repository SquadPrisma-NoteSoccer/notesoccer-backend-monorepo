package com.squadprisma.notesoccer.orchestration_service.application.port.out;

import com.squadprisma.notesoccer.orchestration_service.api.dto.*;

import java.util.List;
import java.util.UUID;

public interface LeagueServicePort {
    LigaResponse criarLiga(CreateLigaRequest request);
    TimeResponse criarTime(CreateTimeRequest request);
    List<TimeResponse> criarTimesLote(UUID ligaId, List<CreateTimeLoteRequest> request);
    PageResponse<TimeResponse> listarTimes(UUID ligaId, int page, int size);
    TimeCountResponse contarTimes(UUID ligaId);
    void deletarTime(UUID timeId, UUID ligaId);
    void deletarLiga(UUID ligaId);
    PageResponse<LigaResponse> listarLigasPorUsuario(UUID userId, int page, int size);
}
