package com.squadprisma.notesoccer.orchestration_service.application.service;

import com.squadprisma.notesoccer.orchestration_service.api.dto.*;
import com.squadprisma.notesoccer.orchestration_service.application.port.out.LeagueServicePort;
import feign.FeignException;
import feign.RetryableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Testes unitários do caso de uso que integra com o league-service via porta (LeagueServicePort).
 * Cenários cobertos:
 *  - Sucesso
 *  - 409 (duplicidade) mapeado para ResponseStatusException CONFLICT
 *  - RetryableException (timeout/conexão) mapeado para 504 GATEWAY_TIMEOUT
 *  - Feign genérico 5xx mapeado para 502 BAD_GATEWAY
 */
@ExtendWith(MockitoExtension.class)
public class LeagueOrchestrationServiceTest {

    @Mock
    LeagueServicePort port;

    @InjectMocks
    LeagueOrchestrationService service;

    private CreateLigaRequest ligaReq;
    private LigaResponse ligaResp;

    private UUID ligaId;
    private String timeNome;
    private TimeResponse timeResp;

    @BeforeEach
    void setup() {
        ligaReq = new CreateLigaRequest("Liga Zona Norte");
        ligaResp = new LigaResponse(UUID.randomUUID(), "Liga Zona Norte", Instant.now());

        ligaId = UUID.randomUUID();
        timeNome = "Atlético Jardim";
        timeResp = new TimeResponse(UUID.randomUUID(), ligaId, timeNome, Instant.now());
    }

    // ====== criarLiga ======

    @Test
    void criarLiga_deveDelegarParaPorta_eRetornarResposta() {
        when(port.criarLiga(ligaReq)).thenReturn(ligaResp);

        var out = service.criarLiga(ligaReq);

        assertThat(out).isEqualTo(ligaResp);
        verify(port).criarLiga(ligaReq);
        verifyNoMoreInteractions(port);
    }

    @Test
    void criarLiga_deveMapear409ParaResponseStatusExceptionConflict() {
        FeignException.Conflict conflict = mock(FeignException.Conflict.class);
        when(conflict.contentUTF8()).thenReturn("LEAGUE_ALREADY_EXISTS");
        when(port.criarLiga(any())).thenThrow(conflict);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.criarLiga(ligaReq));

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(ex.getReason()).isEqualTo("LEAGUE_ALREADY_EXISTS");
        verify(port).criarLiga(ligaReq);
        verifyNoMoreInteractions(port);
    }

    @Test
    void criarLiga_deveMapearRetryableExceptionPara504GatewayTimeout() {
        RetryableException retryable = mock(RetryableException.class);
        when(port.criarLiga(any())).thenThrow(retryable);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.criarLiga(ligaReq));

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.GATEWAY_TIMEOUT);
        assertThat(ex.getReason()).isEqualTo("league-service timeout");
        verify(port).criarLiga(ligaReq);
        verifyNoMoreInteractions(port);
    }

    @Test
    void criarLiga_deveMapearFeignGenericoPara502BadGateway() {
        FeignException generic = mock(FeignException.class);
        when(generic.status()).thenReturn(503);
        when(port.criarLiga(any())).thenThrow(generic);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.criarLiga(ligaReq));

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(ex.getReason()).isEqualTo("league-service error: 503");
        verify(port).criarLiga(ligaReq);
        verifyNoMoreInteractions(port);
    }

    // ====== criarTime ======

    @Test
    void criarTime_deveDelegarParaPorta_eRetornarResposta() {
        // service.criarTime monta CreateTimeRequest(ligaId, nome) e delega
        when(port.criarTime(any(CreateTimeRequest.class))).thenReturn(timeResp);

        var out = service.criarTime(ligaId, timeNome);

        assertThat(out).isEqualTo(timeResp);
        verify(port).criarTime(argThat(req ->
                req.ligaId().equals(ligaId) && req.nome().equals(timeNome)));
        verifyNoMoreInteractions(port);
    }

    @Test
    void criarTime_deveMapear409ParaResponseStatusExceptionConflict() {
        FeignException.Conflict conflict = mock(FeignException.Conflict.class);
        when(conflict.contentUTF8()).thenReturn("TEAM_ALREADY_EXISTS");
        when(port.criarTime(any())).thenThrow(conflict);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.criarTime(ligaId, timeNome));

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(ex.getReason()).isEqualTo("TEAM_ALREADY_EXISTS");
        verify(port).criarTime(any());
        verifyNoMoreInteractions(port);
    }

    @Test
    void criarTime_deveMapearRetryableExceptionPara504GatewayTimeout() {
        RetryableException retryable = mock(RetryableException.class);
        when(port.criarTime(any())).thenThrow(retryable);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.criarTime(ligaId, timeNome));

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.GATEWAY_TIMEOUT);
        assertThat(ex.getReason()).isEqualTo("league-service timeout");
        verify(port).criarTime(any());
        verifyNoMoreInteractions(port);
    }

    @Test
    void criarTime_deveMapearFeignGenericoPara502BadGateway() {
        FeignException generic = mock(FeignException.class);
        when(generic.status()).thenReturn(502);
        when(port.criarTime(any())).thenThrow(generic);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.criarTime(ligaId, timeNome));

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(ex.getReason()).isEqualTo("league-service error: 502");
        verify(port).criarTime(any());
        verifyNoMoreInteractions(port);
    }

    // ====== listar/contar (sucesso) ======

    @Test
    void listarTimes_deveDelegarParaPorta() {
        var page = new PageResponse<TimeResponse>(List.of(timeResp), 0, 20, 1, 1);
        when(port.listarTimes(ligaId, 0, 20)).thenReturn(page);

        var out = service.listarTimes(ligaId, 0, 20);

        assertThat(out.totalElements()).isEqualTo(1);
        verify(port).listarTimes(ligaId, 0, 20);
        verifyNoMoreInteractions(port);
    }

    @Test
    void contarTimes_deveDelegarParaPorta() {
        var count = new TimeCountResponse(ligaId, 1);
        when(port.contarTimes(ligaId)).thenReturn(count);

        var out = service.contarTimes(ligaId);

        assertThat(out.count()).isEqualTo(1);
        verify(port).contarTimes(ligaId);
        verifyNoMoreInteractions(port);
    }
}
