package com.squadprisma.notesoccer.league_service.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LigaCreateRequest(
        @NotBlank @Size(min = 3, max = 80)
        String nome
) {
}
