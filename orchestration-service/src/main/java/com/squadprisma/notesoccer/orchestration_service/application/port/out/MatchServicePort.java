package com.squadprisma.notesoccer.orchestration_service.application.port.out;

import com.squadprisma.notesoccer.orchestration_service.api.dto.CreatePartidaRequest;
import com.squadprisma.notesoccer.orchestration_service.api.dto.PartidaResponse;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface MatchServicePort {
    PartidaResponse criar(CreatePartidaRequest req);
    List<PartidaResponse> calendario(UUID ligaId, OffsetDateTime from, OffsetDateTime to);
    }
