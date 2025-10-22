package com.squadprisma.notesoccer.orchestration_service.api.dto;

import java.time.Instant;
import java.util.UUID;

public record LigaResponse(
        UUID id,
        String nome,
        Instant createdAt
) {
}
