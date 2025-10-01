package com.squadprisma.notesoccer.orchestration_service.application.service;

import com.squadprisma.notesoccer.orchestration_service.api.dto.CreateUsuarioRequest;
import com.squadprisma.notesoccer.orchestration_service.api.dto.UsuarioResponse;
import com.squadprisma.notesoccer.orchestration_service.application.port.out.UserServicePort;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários do caso de uso que integra com o user-service via porta (UserServicePort).
 * Focamos em: sucesso, 409-CONFLICT, timeout/retryable (504) e erro genérico (502).
 */
@ExtendWith(MockitoExtension.class)
public class UserOrchestrationServiceTest {

    @Mock
    UserServicePort userPort;

    @InjectMocks
    UserOrchestrationService service;

    private CreateUsuarioRequest request;
    private UsuarioResponse response;

    @BeforeEach
    void setup(){
        request = new CreateUsuarioRequest(
                "Leonardo",
                "leonardo@exemplo2.com",
                "12345678",
                "Leo",
                "+55 74 99813-1055"
        );

        response = new UsuarioResponse(
                UUID.randomUUID(),
                "Leonardo",
                "leonardo@exemplo2.com",
                "Leo",
                "+55 74 99813-1055"
        );
    }

    @Test
    void criarUsuario_deveDelegarParaPorta_eRetornarResposta(){
        // given
        when(userPort.criarUsuario(request)).thenReturn(response);

        // when
        var out = service.criarUsuario(request);

        //then
        assertThat(out).isEqualTo(response);
        verify(userPort, times(1)).criarUsuario(request);
        verifyNoMoreInteractions(userPort);
    }

    @Test
    void criarUsuario_deveMapear409ParaResponseStatusExceptionConflict(){
        // given: simulamos um 409 do Feign
        FeignException.Conflict conflict = mock(FeignException.Conflict.class);
        when(conflict.contentUTF8()).thenReturn("EMAIL_ALREADY_EXISTS");
        when(userPort.criarUsuario(any())).thenThrow(conflict);

        // when
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.criarUsuario(request));

        // then
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(ex.getReason()).isEqualTo("EMAIL_ALREADY_EXISTS");
        verify(userPort).criarUsuario(request);
        verifyNoMoreInteractions(userPort);
    }

    @Test
    void criarUsuario_deveMapearRetryableExceptionPara504GatewayTimeout() {
        // given: timeout/conexão (Feign lança RetryableException)
        RetryableException retryable = mock(RetryableException.class);
        when(userPort.criarUsuario(any())).thenThrow(retryable);

        // when
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.criarUsuario(request));

        // then
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.GATEWAY_TIMEOUT);
        assertThat(ex.getReason()).isEqualTo("user-service timeout");
        verify(userPort).criarUsuario(request);
        verifyNoMoreInteractions(userPort);
    }

    @Test
    void criarUsuario_deveMapearFeignGenericoPara502BadGateway() {
        // given: erro genérico do Feign (ex.: 5xx qualquer)
        FeignException generic = mock(FeignException.class);
        when(generic.status()).thenReturn(503);
        when(userPort.criarUsuario(any())).thenThrow(generic);

        // when
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.criarUsuario(request));

        // then
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(ex.getReason()).isEqualTo("user-service error: 503");
        verify(userPort).criarUsuario(request);
        verifyNoMoreInteractions(userPort);
    }
}
