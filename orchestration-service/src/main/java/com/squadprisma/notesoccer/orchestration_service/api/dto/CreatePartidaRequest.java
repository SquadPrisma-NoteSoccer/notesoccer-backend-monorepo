package com.squadprisma.notesoccer.orchestration_service.api.dto;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CreatePartidaRequest(
        @NotNull UUID ligaId,
        @NotNull UUID casaTimeId,
        @NotNull UUID visitanteTimeId,
        @NotNull OffsetDateTime startAt,
        @NotNull OffsetDateTime endAt,
        @NotNull String local,
        String notas
) {
}
