package com.squadprisma.notesoccer.user_service.repository;

import com.squadprisma.notesoccer.user_service.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    boolean existsByEmailIgnoreCase(String email);
}
