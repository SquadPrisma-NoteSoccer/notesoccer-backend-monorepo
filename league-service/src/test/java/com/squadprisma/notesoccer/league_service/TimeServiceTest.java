package com.squadprisma.notesoccer.league_service;


import com.squadprisma.notesoccer.league_service.api.dto.TimeCreateRequest;
import com.squadprisma.notesoccer.league_service.domain.entity.Liga;
import com.squadprisma.notesoccer.league_service.domain.entity.Time;
import com.squadprisma.notesoccer.league_service.domain.exception.BadRequestException;
import com.squadprisma.notesoccer.league_service.domain.exception.ConflictException;
import com.squadprisma.notesoccer.league_service.domain.exception.NotFoundException;
import com.squadprisma.notesoccer.league_service.repository.LigaRepository;
import com.squadprisma.notesoccer.league_service.repository.TimeRepository;
import com.squadprisma.notesoccer.league_service.service.TimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TimeServiceTest {
    TimeRepository timeRepo;
    LigaRepository ligaRepo;
    TimeService service;

    final UUID LIGA_ID = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
    Liga liga;

    @BeforeEach
    void setUp() {
        timeRepo = mock(TimeRepository.class);
        ligaRepo = mock(LigaRepository.class);
        service = new TimeService(timeRepo, ligaRepo);

        liga = new Liga();
        liga.setId(LIGA_ID);
        liga.setNome("Liga Teste");
    }

    @Test
    void create_ok() {
        when(ligaRepo.findById(LIGA_ID)).thenReturn(Optional.of(liga));
        when(timeRepo.countByLiga(liga)).thenReturn(0L);
        when(timeRepo.existsByLigaAndNomeIgnoreCase(liga, "Atlético Jardim")).thenReturn(false);

        var req = new TimeCreateRequest(LIGA_ID, "  Atlético   Jardim  ");
        Time saved = new Time();
        saved.setLiga(liga);
        saved.setNome("Atlético Jardim");
        when(timeRepo.save(any(Time.class))).thenReturn(saved);

        var time = service.create(req);

        // garante que “trim + normalização de espaços” aconteceu
        ArgumentCaptor<Time> captor = ArgumentCaptor.forClass(Time.class);
        verify(timeRepo).save(captor.capture());
        assertThat(captor.getValue().getNome()).isEqualTo("Atlético Jardim");

        assertThat(time.getLiga().getId()).isEqualTo(LIGA_ID);
    }

    @Test
    void create_league_nao_encontrada() {
        when(ligaRepo.findById(LIGA_ID)).thenReturn(Optional.empty());
        var req = new TimeCreateRequest(LIGA_ID, "Time X");

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("LEAGUE_NOT_FOUND");
    }

    @Test
    void create_limite_atingido_20() {
        when(ligaRepo.findById(LIGA_ID)).thenReturn(Optional.of(liga));
        when(timeRepo.countByLiga(liga)).thenReturn(20L);

        var req = new TimeCreateRequest(LIGA_ID, "Time X");

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(ConflictException.class)
                .hasMessage("TEAM_LIMIT_REACHED");
    }

    @Test
    void create_nome_duplicado_na_mesma_liga_case_insensitive() {
        when(ligaRepo.findById(LIGA_ID)).thenReturn(Optional.of(liga));
        // menor que 20 para não cair no TEAM_LIMIT_REACHED antes
        when(timeRepo.countByLiga(liga)).thenReturn(5L);
        // garante match independente de normalização/case
        when(timeRepo.existsByLigaAndNomeIgnoreCase(eq(liga), anyString()))
                .thenReturn(true);

        var req = new TimeCreateRequest(LIGA_ID, "tImE x");

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(ConflictException.class)
                .hasMessage("TEAM_ALREADY_EXISTS");

        verify(timeRepo, times(1)).existsByLigaAndNomeIgnoreCase(eq(liga), anyString());
        verify(timeRepo, never()).save(any());
    }

    @Test
    void delete_ok() {
        UUID timeId = UUID.randomUUID();

        Time t = new Time();
        t.setId(timeId);
        t.setLiga(liga);

        when(timeRepo.findById(timeId)).thenReturn(Optional.of(t));

        service.delete(LIGA_ID, timeId);

        verify(timeRepo, times(1)).delete(t);
    }

    @Test
    void delete_time_nao_encontrado() {
        UUID timeId = UUID.randomUUID();

        when(timeRepo.findById(timeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(LIGA_ID, timeId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("TEAM_NOT_FOUND");
    }

    @Test
    void delete_time_nao_pertence_a_liga() {
        UUID timeId = UUID.randomUUID();

        Liga outraLiga = new Liga();
        outraLiga.setId(UUID.randomUUID());

        Time t = new Time();
        t.setId(timeId);
        t.setLiga(outraLiga);

        when(timeRepo.findById(timeId)).thenReturn(Optional.of(t));

        assertThatThrownBy(() -> service.delete(LIGA_ID, timeId))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("TEAM_NOT_IN_LEAGUE");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void create_nome_em_branco(String name) {
        var req = new TimeCreateRequest(LIGA_ID, name);
        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("TEAM_NAME_REQUIRED");
    }
}
