package com.squadprisma.notesoccer.user_service.api.controller;

import com.squadprisma.notesoccer.user_service.api.dto.CadastroUsuarioDTO;
import com.squadprisma.notesoccer.user_service.api.dto.CadastroUsuarioResponse;
import com.squadprisma.notesoccer.user_service.domain.entity.Usuario;
import com.squadprisma.notesoccer.user_service.repository.UsuarioRepository;
import com.squadprisma.notesoccer.user_service.service.UsuarioMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/usuarios")
public class UsuarioController {

    private final UsuarioMapper mapper;
    private final UsuarioRepository repository;

    public UsuarioController(UsuarioMapper mapper, UsuarioRepository repository) {
        this.mapper = mapper;
        this.repository = repository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CadastroUsuarioResponse create(@RequestBody @Valid CadastroUsuarioDTO dto) {
        Usuario salvo = repository.save(mapper.toEntity(dto));
        return new CadastroUsuarioResponse(salvo.getId(), salvo.getNome(), salvo.getEmail(),
                salvo.getApelido(), salvo.getWhatsappE164());
    }
}
