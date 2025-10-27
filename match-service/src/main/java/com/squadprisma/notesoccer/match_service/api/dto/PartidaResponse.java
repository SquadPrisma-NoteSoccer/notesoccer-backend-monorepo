package com.squadprisma.notesoccer.match_service.api.dto;

import com.squadprisma.notesoccer.match_service.domain.enums.PartidaStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PartidaResponse(
        UUID id, UUID ligaId, UUID casaTimeId, UUID visitanteTimeId,
        OffsetDateTime startAt, OffsetDateTime endAt,
        String local, String notes, PartidaStatus status
) {
}
