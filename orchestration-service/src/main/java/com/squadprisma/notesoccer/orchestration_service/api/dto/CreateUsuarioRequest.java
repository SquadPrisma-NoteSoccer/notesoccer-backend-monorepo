package com.squadprisma.notesoccer.orchestration_service.api.dto;

//request
public record CreateUsuarioRequest(
        String nome, String email, String senha, String apelido, String whatsapp) {}
