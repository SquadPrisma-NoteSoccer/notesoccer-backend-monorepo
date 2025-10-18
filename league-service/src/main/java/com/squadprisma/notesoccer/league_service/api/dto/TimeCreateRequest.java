package com.squadprisma.notesoccer.league_service.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record TimeCreateRequest(
        @NotNull UUID ligaId,
        @NotBlank
        @Size(min = 3, max = 50)
        @Pattern(regexp = "^[\\p{L}0-9\\- ]+$",
                message = "Somente letras, números, espaço e hífen")
        String nome
) {
}
