package com.squadprisma.notesoccer.league_service.api.dto;

import java.time.Instant;
import java.util.UUID;

public record TimeResponse(UUID id, UUID ligaId, String nome, Instant createdAt) {
}
