package com.squadprisma.notesoccer.orchestration_service.api.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SagaStatusResponse(
        UUID id,
        String type,
        String status,
        String currentStep,
        String data,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
