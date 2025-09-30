package com.squadprisma.notesoccer.user_service.api.dto;

import java.util.UUID;

public record CadastroUsuarioResponse(
        UUID id,
        String nome,
        String email,
        String apelido,
        String whatsapp
) {
}
