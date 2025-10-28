package com.squadprisma.notesoccer.orchestration_service.api.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PartidaResponse(
        UUID id, UUID ligaId, UUID casaTimeId, UUID visitanteTimeId,
        OffsetDateTime startAt, OffsetDateTime endAt,
        String local, String notas, String status
) {
}
