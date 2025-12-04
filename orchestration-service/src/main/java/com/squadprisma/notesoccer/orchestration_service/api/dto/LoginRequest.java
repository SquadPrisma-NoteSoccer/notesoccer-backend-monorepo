package com.squadprisma.notesoccer.orchestration_service.api.dto;

public record LoginRequest(
        String email,
        String senha
) {
}
