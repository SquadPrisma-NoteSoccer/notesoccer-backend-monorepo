package com.squadprisma.notesoccer.user_service.service;

import com.squadprisma.notesoccer.user_service.api.dto.CadastroUsuarioDTO;
import com.squadprisma.notesoccer.user_service.domain.entity.Usuario;
import com.squadprisma.notesoccer.user_service.domain.exception.EmailAlreadyInUseException;
import com.squadprisma.notesoccer.user_service.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UsuarioService {
    private final UsuarioRepository repository;
    private final UsuarioMapper mapper;

    public UsuarioService(UsuarioRepository repository, UsuarioMapper mapper){
        this.repository = repository;
        this.mapper = mapper;
    }

    public Usuario criar(CadastroUsuarioDTO dto){
        String email = dto.email().trim().toLowerCase();
        if (repository.existsByEmailIgnoreCase(email)){
            throw new EmailAlreadyInUseException(email);
        }
        return repository.save(mapper.toEntity(dto));
    }
}
