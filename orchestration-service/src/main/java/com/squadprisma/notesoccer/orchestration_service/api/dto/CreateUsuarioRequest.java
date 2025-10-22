package com.squadprisma.notesoccer.orchestration_service.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

//request
public record CreateUsuarioRequest(
        @NotBlank @Size(min = 3, max = 80)String nome,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 80) String senha,
        @NotBlank @Size(min = 3, max = 40) String apelido,
        @NotBlank @Size(min = 8, max = 30) String whatsapp) {}
