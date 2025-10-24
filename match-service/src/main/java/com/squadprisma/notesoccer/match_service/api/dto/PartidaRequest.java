package com.squadprisma.notesoccer.match_service.api.dto;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PartidaRequest(
        @NotNull UUID ligaId,
        @NotNull UUID casaTimeId,
        @NotNull UUID visitanteTimeId,
        @NotNull OffsetDateTime startAt,
        @NotNull OffsetDateTime endAt,
        String local,
        String notas
) {
}
