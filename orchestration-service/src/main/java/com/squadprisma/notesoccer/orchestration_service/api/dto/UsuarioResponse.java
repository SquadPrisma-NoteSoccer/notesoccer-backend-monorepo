package com.squadprisma.notesoccer.orchestration_service.api.dto;

import java.util.UUID;

//response
public record UsuarioResponse(
        UUID id, String nome, String email, String apelido, String whatsapp) {}
