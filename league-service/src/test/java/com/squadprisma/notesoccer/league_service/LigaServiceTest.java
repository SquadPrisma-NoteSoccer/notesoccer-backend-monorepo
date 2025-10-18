package com.squadprisma.notesoccer.league_service;

import com.squadprisma.notesoccer.league_service.api.dto.LigaCreateRequest;
import com.squadprisma.notesoccer.league_service.domain.entity.Liga;
import com.squadprisma.notesoccer.league_service.repository.LigaRepository;
import com.squadprisma.notesoccer.league_service.service.LigaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    void create_ok_trim_e_normaliza_espacos() {
        var req = new LigaCreateRequest("  Liga    Zona   Norte  ");
        when(repo.save(any(Liga.class))).thenAnswer(inv -> inv.getArgument(0));

        var liga = service.create(req);

        assertThat(liga.getNome()).isEqualTo("Liga Zona Norte");
        verify(repo, times(1)).save(any(Liga.class));
    }


}
