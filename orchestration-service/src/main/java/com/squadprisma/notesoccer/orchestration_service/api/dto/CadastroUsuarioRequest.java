package com.squadprisma.notesoccer.orchestration_service.api.dto;

public record CadastroUsuarioRequest(
        String nome,
        String email,
        String senha,
        String apelido,
        String whatsapp
) {
}
