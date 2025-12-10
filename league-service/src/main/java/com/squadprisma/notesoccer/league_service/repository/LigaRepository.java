package com.squadprisma.notesoccer.league_service.repository;

import com.squadprisma.notesoccer.league_service.domain.entity.Liga;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LigaRepository extends JpaRepository<Liga, UUID> {
    Page<Liga> findByUserId(UUID userId, Pageable pageable);
}
