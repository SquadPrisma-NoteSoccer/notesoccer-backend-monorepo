package com.squadprisma.notesoccer.user_service.service;

import com.squadprisma.notesoccer.user_service.api.dto.CadastroUsuarioDTO;
import com.squadprisma.notesoccer.user_service.domain.entity.Usuario;
import com.squadprisma.notesoccer.user_service.domain.enums.Role;
import com.squadprisma.notesoccer.user_service.domain.exception.EmailAlreadyInUseException;
import com.squadprisma.notesoccer.user_service.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UsuarioService {
    private final UsuarioRepository repository;
    private final UsuarioMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, UsuarioMapper mapper, PasswordEncoder passwordEncoder){
        this.repository = repository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario criar(CadastroUsuarioDTO dto){
        String email = dto.email().trim().toLowerCase();
        if (repository.existsByEmailIgnoreCase(email)){
            throw new EmailAlreadyInUseException(email);
        }

        Usuario usuario = mapper.toEntity(dto);

        // garante que o email utilizado será o normalizado
        usuario.setEmail(email);

        // criptografa a senha vinda do DTO
        // 👉 ajuste aqui o nome do getter se no DTO o campo tiver outro nome (ex: dto.senhaPlano())
        usuario.setSenhaHash(passwordEncoder.encode(dto.senha()));

        // define role padrão
        usuario.setRole(Role.ROLE_ORGANIZADOR);

        return repository.save(usuario);
    }
}
