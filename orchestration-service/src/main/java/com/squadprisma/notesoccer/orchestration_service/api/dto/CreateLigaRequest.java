package com.squadprisma.notesoccer.orchestration_service.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateLigaRequest(
        @NotBlank @Size(min = 3, max = 80)
        String nome,
        @NotNull UUID userId
) {
}
