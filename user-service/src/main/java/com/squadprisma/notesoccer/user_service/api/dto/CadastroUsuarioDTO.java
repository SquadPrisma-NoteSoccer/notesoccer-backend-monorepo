package com.squadprisma.notesoccer.user_service.api.dto;

import com.squadprisma.notesoccer.user_service.validation.BrazilPhone;
import com.squadprisma.notesoccer.user_service.validation.MaxSpecialChars;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CadastroUsuarioDTO(
        // Nome: obrigatório, apenas letras (com acentos) e espaço, 3–60, com pelo menos 3 letras
        @NotBlank(message = "O nome é obrigatório.")
        @Size(min = 3, max = 60, message = "O nome deve ter entre 3 e 60 caracteres.")
        @Pattern(
                regexp = "^(?=(?:.*\\p{L}){3,})[\\p{L} ]+$",
                message = "O nome deve conter apenas letras e espaços."
        )
        String nome,

        // E-mail: obrigatório, formato válido, sem espaços, até 60
        @NotBlank(message = "O e-mail é obrigatório.")
        @Size(max = 60, message = "O e-mail deve ter no máximo 60 caracteres.")
        @Email(message = "Formato de e-mail inválido.")
        String email,

        // Senha: obrigatória, 8–15; máx. 12 caracteres especiais
        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = 8, max = 15, message = "A senha deve ter entre 8 e 15 caracteres.")
        @MaxSpecialChars(max = 12, message = "A senha excede o limite de 12 caracteres especiais.")
        String senha,

        // Apelido: opcional, sem caracteres especiais, 0 ou 3–60 c/ pelo menos 3 letras
        @Size(max = 60, message = "O apelido deve ter no máximo 60 caracteres.")
        @Pattern(
                // letras (com acento), dígitos e espaço; se informado, exige ao menos 3 letras
                regexp = "^$|(?=(?:.*\\p{L}){3,})[\\p{L}\\d ]+$",
                message = "O apelido não deve conter caracteres especiais e precisa ter ao menos 3 letras."
        )
        String apelido,

        // WhatsApp: opcional, formato BR até 14 dígitos (com ou sem 55/DDD); só valida se informado
        @BrazilPhone(optional = true)
        String whatsapp
) {

}
