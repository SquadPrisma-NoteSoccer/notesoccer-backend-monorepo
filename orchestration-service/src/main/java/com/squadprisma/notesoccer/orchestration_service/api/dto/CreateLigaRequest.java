package com.squadprisma.notesoccer.orchestration_service.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateLigaRequest(
        @NotBlank @Size(min = 3, max = 80)
        String nome
) {
}
