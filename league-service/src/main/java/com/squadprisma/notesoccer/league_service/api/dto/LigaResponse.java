package com.squadprisma.notesoccer.league_service.api.dto;

import java.time.Instant;
import java.util.UUID;

public record LigaResponse(UUID id, String nome, UUID userId, Instant createdAt) {
}
