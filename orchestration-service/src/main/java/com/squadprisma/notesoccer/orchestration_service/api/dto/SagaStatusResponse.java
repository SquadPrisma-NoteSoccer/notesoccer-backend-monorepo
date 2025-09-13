package com.squadprisma.notesoccer.orchestration_service.api.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SagaStatusResponse(
        UUID id,
        String type,
        String status,
        String currentStep,
        JsonNode data,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
