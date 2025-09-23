package com.squadprisma.notesoccer.user_service.api.controller;

import com.squadprisma.notesoccer.user_service.api.dto.CadastroUsuarioDTO;
import com.squadprisma.notesoccer.user_service.api.dto.CadastroUsuarioResponse;
import com.squadprisma.notesoccer.user_service.domain.entity.Usuario;
import com.squadprisma.notesoccer.user_service.repository.UsuarioRepository;
import com.squadprisma.notesoccer.user_service.service.UsuarioMapper;
import com.squadprisma.notesoccer.user_service.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/usuarios")
public class UsuarioController {

   private final UsuarioService service;
   private final UsuarioRepository repository;

    public UsuarioController(UsuarioService service, UsuarioRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CadastroUsuarioResponse criar(@RequestBody @Valid CadastroUsuarioDTO dto) {
        Usuario salvo = service.criar(dto);
        return new CadastroUsuarioResponse(salvo.getId(), salvo.getNome(), salvo.getEmail(),
                salvo.getApelido(), salvo.getWhatsappE164());
    }
}
