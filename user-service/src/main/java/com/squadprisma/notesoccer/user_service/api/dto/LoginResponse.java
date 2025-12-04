package com.squadprisma.notesoccer.user_service.api.dto;

import java.util.UUID;

public record LoginResponse(
        UUID userId,
        String nome,
        String email,
        String role,
        String token
) {
}
