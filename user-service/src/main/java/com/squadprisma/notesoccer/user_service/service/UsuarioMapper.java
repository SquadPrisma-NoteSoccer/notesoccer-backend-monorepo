package com.squadprisma.notesoccer.user_service.service;

import com.squadprisma.notesoccer.user_service.api.dto.CadastroUsuarioDTO;
import com.squadprisma.notesoccer.user_service.domain.entity.Usuario;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {
    private final PasswordEncoder encoder;

    public UsuarioMapper(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    public Usuario toEntity(CadastroUsuarioDTO dto) {
        return Usuario.builder()
                .nome(dto.nome().trim())
                .email(dto.email())
                .apelido(dto.apelido() == null ? null : dto.apelido().trim())
                .whatsappE164(normalizeE164(dto.whatsapp()))
                .build();
    }

    // Normaliza para +55AA######### quando possível; se não der, retorna nulo/valor bruto
    private String normalizeE164(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String d = raw.replaceAll("\\D", "");
        if (d.length() == 10 || d.length() == 11) { // sem 55
            return "+55" + d;
        } else if (d.length() == 12 || d.length() == 13 || d.length() == 14) { // com 55
            if (d.startsWith("55")) return "+" + d;
        }
        // fallback: mantém só dígitos com + (passou pela validação, então é aceitável)
        return "+" + d;
    }
}
