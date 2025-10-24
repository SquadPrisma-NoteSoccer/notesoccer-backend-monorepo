package com.squadprisma.notesoccer.match_service.service;

import com.squadprisma.notesoccer.match_service.domain.entity.Partida;
import com.squadprisma.notesoccer.match_service.domain.enums.PartidaStatus;
import com.squadprisma.notesoccer.match_service.repository.PartidaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class PartidaServiceTest {

    private PartidaRepository repo;
    private PartidaService service;

    private final UUID ligaId = UUID.randomUUID();
    private final UUID casaTimeId = UUID.randomUUID();
    private final UUID visitanteTimeId = UUID.randomUUID();
    private final OffsetDateTime start = OffsetDateTime.parse("2025-07-22T09:00:00-03:00");
    private final OffsetDateTime end   = OffsetDateTime.parse("2025-07-22T10:30:00-03:00");

    @BeforeEach
    void setUp() {
        repo = mock(PartidaRepository.class);
        service = new PartidaService(repo);
    }

    private Partida buildPartida() {
        Partida p = Partida.builder()
                .ligaId(ligaId)
                .casaTimeId(casaTimeId)
                .visitanteTimeId(visitanteTimeId)
                .startAt(start)
                .endAt(end)
                .local("Quadra A")
                .notas("Levar coletes")
                .status(PartidaStatus.AGENDADA)
                .build();
        return p;
    }

    @Test
    void create_ok_saves_when_no_conflict() {
        Partida toSave = buildPartida();
        Partida saved = buildPartida();
        saved.setId(UUID.randomUUID());
        when(repo.findConflicts(ligaId, casaTimeId, visitanteTimeId, start, end)).thenReturn(List.of());
        when(repo.save(any(Partida.class))).thenReturn(saved);

        Partida result = service.criar(toSave);

        // verifica chamada de conflito e persistência
        verify(repo).findConflicts(ligaId, casaTimeId, visitanteTimeId, start, end);
        ArgumentCaptor<Partida> cap = ArgumentCaptor.forClass(Partida.class);
        verify(repo).save(cap.capture());

        assertThat(result.getId()).isNotNull();
        assertThat(cap.getValue().getStatus()).isEqualTo(PartidaStatus.AGENDADA);
    }

    @Test
    void create_throws_when_same_team() {
        Partida p = buildPartida();
        p.setVisitanteTimeId(p.getCasaTimeId());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.criar(p));
        assertThat(ex.getMessage()).contains("Times devem ser distintos");
        verifyNoInteractions(repo);
    }

    @Test
    void create_throws_when_invalid_time_range() {
        Partida p = buildPartida();
        p.setEndAt(p.getStartAt());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.criar(p));
        assertThat(ex.getMessage()).contains("Horário inválido");
        verifyNoInteractions(repo);
    }

    @Test
    void create_throws_on_conflict() {
        Partida p = buildPartida();
        when(repo.findConflicts(ligaId, casaTimeId, visitanteTimeId, start, end)).thenReturn(List.of(new Partida()));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service.criar(p));
        assertThat(ex.getMessage()).contains("Conflito de agenda");
        verify(repo, never()).save(any());
    }

    @Test
    void calendar_returns_items_from_repo() {
        OffsetDateTime from = OffsetDateTime.parse("2025-07-01T00:00:00-03:00");
        OffsetDateTime to   = OffsetDateTime.parse("2025-07-31T23:59:59-03:00");

        when(repo.findByLigaIdAndStartAtBetween(ligaId, from, to)).thenReturn(List.of(buildPartida()));

        var list = service.calendario(ligaId, from, to);

        assertThat(list).hasSize(1);
        verify(repo).findByLigaIdAndStartAtBetween(ligaId, from, to);
    }
}
