package com.squadprisma.notesoccer.orchestration_service.api.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StartSagaRequest(
        @NotBlank String type,
        @NotNull JsonNode payload
) {
}
