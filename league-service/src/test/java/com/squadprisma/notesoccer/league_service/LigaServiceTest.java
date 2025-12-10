package com.squadprisma.notesoccer.league_service;

import com.squadprisma.notesoccer.league_service.api.dto.LigaCreateRequest;
import com.squadprisma.notesoccer.league_service.domain.entity.Liga;
import com.squadprisma.notesoccer.league_service.domain.exception.NotFoundException;
import com.squadprisma.notesoccer.league_service.repository.LigaRepository;
import com.squadprisma.notesoccer.league_service.repository.TimeRepository;
import com.squadprisma.notesoccer.league_service.service.LigaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LigaServiceTest {

    LigaRepository repo;
    TimeRepository timeRepo;
    LigaService service;

    @BeforeEach
    void setUp() {
        repo = mock(LigaRepository.class);
        timeRepo = mock(TimeRepository.class);
        service = new LigaService(repo, timeRepo);
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
    void delete_ok_remove_times_e_liga() {
        UUID ligaId = UUID.randomUUID();

        Liga liga = new Liga();
        liga.setId(ligaId);

        when(repo.findById(ligaId)).thenReturn(Optional.of(liga));
        when(timeRepo.deleteByLiga(liga)).thenReturn(3L);

        service.delete(ligaId);

        verify(repo).findById(ligaId);
        verify(timeRepo).deleteByLiga(liga);
        verify(repo).delete(liga);
        verifyNoMoreInteractions(repo, timeRepo);
    }

    @Test
    void delete_liga_nao_encontrada() {
        UUID ligaId = UUID.randomUUID();

        when(repo.findById(ligaId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(ligaId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("LEAGUE_NOT_FOUND");

        verify(repo).findById(ligaId);
        verifyNoMoreInteractions(repo);
        verifyNoInteractions(timeRepo);
    }

}
