package com.squadprisma.notesoccer.user_service.security;

import com.squadprisma.notesoccer.user_service.api.dto.CadastroUsuarioDTO;
import com.squadprisma.notesoccer.user_service.api.dto.LoginRequest;
import com.squadprisma.notesoccer.user_service.api.dto.LoginResponse;
import com.squadprisma.notesoccer.user_service.config.UsuarioDetails;
import com.squadprisma.notesoccer.user_service.domain.entity.Usuario;
import com.squadprisma.notesoccer.user_service.domain.enums.Role;
import com.squadprisma.notesoccer.user_service.repository.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest request) {

        var authToken = new UsernamePasswordAuthenticationToken(
                request.email(),
                request.senha()
        );

        var authentication = authenticationManager.authenticate(authToken);

        var usuarioDetails = (UsuarioDetails) authentication.getPrincipal();
        var usuario = usuarioDetails.getUsuario();

        String token = jwtService.generateToken(usuario);

        return new LoginResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole().name(),
                token
        );
    }

    public LoginResponse signup(CadastroUsuarioDTO request) {

        // valida email único – adapta pra exceção customizada se você já tiver
        if (usuarioRepository.existsByEmailIgnoreCase(request.email())) {
            throw new IllegalArgumentException("E-mail já cadastrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setApelido(request.apelido());
        usuario.setEmail(request.email());
        usuario.setWhatsappE164(request.whatsapp());
        usuario.setSenhaHash(passwordEncoder.encode(request.senha()));
        // define role padrão – ajusta se tiver outra estratégia
        usuario.setRole(Role.ROLE_ORGANIZADOR);

        usuario = usuarioRepository.save(usuario);

        String token = jwtService.generateToken(usuario);

        return new LoginResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole().name(),
                token
        );
    }


}
