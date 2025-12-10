package com.squadprisma.notesoccer.orchestration_service.infra.clients;

import com.squadprisma.notesoccer.orchestration_service.api.dto.*;
import com.squadprisma.notesoccer.orchestration_service.application.port.out.LeagueServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LeagueServiceFeignAdapter implements LeagueServicePort {

    private final LeagueServiceClient client;

    @Override
    public LigaResponse criarLiga(CreateLigaRequest request) {
        return client.criarLiga(request);
    }

    @Override
    public TimeResponse criarTime(CreateTimeRequest request) {
        return client.criarTime(request);
    }

    @Override
    public List<TimeResponse> criarTimesLote(UUID ligaId, List<CreateTimeLoteRequest> request) {
        return client.criarTimesLote(ligaId, request);
    }

    @Override
    public PageResponse<TimeResponse> listarTimes(UUID ligaId, int page, int size) {
        return client.listarTimes(ligaId, page, size);
    }

    @Override
    public TimeCountResponse contarTimes(UUID ligaId) {
        return client.contarTimes(ligaId);
    }

    @Override
    public void deletarTime(UUID timeId, UUID ligaId) {
        client.deleteTime(timeId, ligaId);
    }

    @Override
    public void deletarLiga(UUID ligaId) {
        client.deleteLeague(ligaId);
    }

    @Override
    public PageResponse<LigaResponse> listarLigasPorUsuario(UUID userId, int page, int size) {
        return client.listLeaguesByUser(userId, page, size);
    }
}
