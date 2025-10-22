package com.squadprisma.notesoccer.orchestration_service.api.dto;

import java.util.UUID;

public record TimeCountResponse(
        UUID ligaId,
        long count
) {
}
