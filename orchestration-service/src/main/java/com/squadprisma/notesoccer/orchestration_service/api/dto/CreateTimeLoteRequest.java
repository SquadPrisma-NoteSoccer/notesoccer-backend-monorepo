package com.squadprisma.notesoccer.orchestration_service.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateTimeLoteRequest(
        @NotBlank @Size(min = 3, max = 50)
        String nome
) {
}
