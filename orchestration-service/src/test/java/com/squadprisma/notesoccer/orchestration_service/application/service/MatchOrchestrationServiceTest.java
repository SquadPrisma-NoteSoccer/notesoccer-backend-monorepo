package com.squadprisma.notesoccer.orchestration_service.application.service;

import com.squadprisma.notesoccer.orchestration_service.api.dto.CreatePartidaRequest;
import com.squadprisma.notesoccer.orchestration_service.api.dto.PartidaResponse;
import com.squadprisma.notesoccer.orchestration_service.application.port.out.MatchServicePort;
import com.squadprisma.notesoccer.orchestration_service.domain.exception.ConflictException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MatchOrchestrationServiceTest {

    @Mock
    MatchServicePort port;

    @InjectMocks
    MatchOrchestrationService service;

    @Test
    void criarDeveChamarFeign_comPayloadCorreto() {
        var req = new CreatePartidaRequest(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                OffsetDateTime.now().plusDays(1),
                OffsetDateTime.now().plusDays(1).plusHours(2), "Quadra A", null
        );
        var esperado = new PartidaResponse(UUID.randomUUID(), UUID.randomUUID(), req.casaTimeId(), req.visitanteTimeId(),
                req.startAt(), req.endAt(), req.local(), req.notas(), "AGENDADA");

        when(port.criar(req)).thenReturn(esperado);

        var resp = service.criar(req);

        verify(port, times(1)).criar(req);
        assertThat(resp.status()).isEqualTo("AGENDADA");
    }

    @Test
    void criarDeveFalhar_quandoStartDepoisDoEnd() {
        var req = new CreatePartidaRequest(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                OffsetDateTime.now().plusDays(1).plusHours(3),
                OffsetDateTime.now().plusDays(1), "Quadra A", null
        );

        assertThatThrownBy(() -> service.criar(req))
                .isInstanceOf(ConflictException.class);
        verifyNoInteractions(port);
    }
}
