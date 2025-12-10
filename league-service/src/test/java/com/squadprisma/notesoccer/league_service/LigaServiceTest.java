package com.squadprisma.notesoccer.league_service;

import com.squadprisma.notesoccer.league_service.api.dto.LigaCreateRequest;
import com.squadprisma.notesoccer.league_service.domain.entity.Liga;
import com.squadprisma.notesoccer.league_service.domain.exception.NotFoundException;
import com.squadprisma.notesoccer.league_service.repository.LigaRepository;
import com.squadprisma.notesoccer.league_service.service.LigaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LigaServiceTest {

    LigaRepository repo;
    LigaService service;

    @BeforeEach
    void setUp() {
        repo = mock(LigaRepository.class);
        service = new LigaService(repo);
    }

    @Test
    void criar_ok_trim_e_normaliza_espacos() {
        UUID ligaId = UUID.randomUUID();
        var req = new LigaCreateRequest("  Liga    Zona   Norte  ", ligaId);
        when(repo.save(any(Liga.class))).thenAnswer(inv -> inv.getArgument(0));

        var liga = service.criar(req);

        assertThat(liga.getNome()).isEqualTo("Liga Zona Norte");
        assertThat(liga.getUserId()).isEqualTo(ligaId);
        verify(repo, times(1)).save(any(Liga.class));
    }

    @Test
    void delete_ok() {
        UUID ligaId = UUID.randomUUID();

        when(repo.existsById(ligaId)).thenReturn(true);

        service.delete(ligaId);

        verify(repo, times(1)).existsById(ligaId);
        verify(repo, times(1)).deleteByLigaId(ligaId);
    }

    @Test
    void delete_liga_nao_encontrada() {
        UUID ligaId = UUID.randomUUID();

        when(repo.existsById(ligaId)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(ligaId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("LEAGUE_NOT_FOUND");

        verify(repo, never()).deleteByLigaId(any());
    }

}
