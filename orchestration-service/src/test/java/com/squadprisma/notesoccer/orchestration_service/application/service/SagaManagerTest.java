package com.squadprisma.notesoccer.orchestration_service.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.squadprisma.notesoccer.orchestration_service.api.dto.SagaStatusResponse;
import com.squadprisma.notesoccer.orchestration_service.api.dto.StartSagaRequest;
import com.squadprisma.notesoccer.orchestration_service.domain.definitions.SagaDefinitions;
import com.squadprisma.notesoccer.orchestration_service.domain.entity.SagaInstance;
import com.squadprisma.notesoccer.orchestration_service.domain.enumtype.SagaStatus;
import com.squadprisma.notesoccer.orchestration_service.domain.port.SagaDispatcher;
import com.squadprisma.notesoccer.orchestration_service.repository.SagaInstanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SagaManagerTest {

    @Mock
    SagaInstanceRepository repository;

    @Mock
    SagaDispatcher dispatcher;

    @InjectMocks
    SagaManager manager;

    ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
    }

    @Test
    void start_devePersistirComRunning_ePrimeiroStep_eDespachar() {
        // given
        String type = "USER_ONBOARDING";
        ObjectNode payload = mapper.createObjectNode().put("userId", "u-1");
        StartSagaRequest req = new StartSagaRequest(type, payload);

        String firstStep = SagaDefinitions.firstStep(type).orElseThrow().name();

        // Simula o save retornando a entidade com ID e timestamps
        when(repository.save(any(SagaInstance.class))).thenAnswer(inv -> {
            SagaInstance e = inv.getArgument(0);
            if (e.getId() == null) e.setId(UUID.randomUUID());
            if (e.getCreatedAt() == null) e.setCreatedAt(OffsetDateTime.now());
            e.setUpdatedAt(OffsetDateTime.now());
            return e;
        });

        // when
        SagaStatusResponse resp = manager.start(req);

        // then: não avança — continua no primeiro step
        assertThat(resp.type()).isEqualTo(type);
        assertThat(resp.status()).isEqualTo(SagaStatus.RUNNING.name());
        assertThat(resp.currentStep()).isEqualTo(firstStep);

        // apenas 1 persistência
        ArgumentCaptor<SagaInstance> captor = ArgumentCaptor.forClass(SagaInstance.class);
        verify(repository, times(1)).save(captor.capture());

        SagaInstance firstPersist = captor.getValue();
        assertThat(firstPersist.getType()).isEqualTo(type);
        assertThat(firstPersist.getStatus()).isEqualTo(SagaStatus.RUNNING);
        assertThat(firstPersist.getCurrentStep()).isEqualTo(firstStep);

        // dispatcher chamado para o primeiro step
        verify(dispatcher).dispatch(eq(type), eq(firstStep), eq(payload));

        verifyNoMoreInteractions(repository, dispatcher);
    }

    @Test
    void advance_deveAvancarParaProximoStep_eDespachar() {
        // given
        UUID id = UUID.randomUUID();
        String type = "USER_ONBOARDING";
        String first = SagaDefinitions.firstStep(type).orElseThrow().name();                      // INIT
        String next  = SagaDefinitions.nextStep(type, first).orElseThrow().name();               // VALIDATE_INPUT

        SagaInstance e = SagaInstance.builder()
                .id(id)
                .type(type)
                .status(SagaStatus.RUNNING)
                .currentStep(first) // começa em INIT
                .data(mapper.createObjectNode().put("userId", "u-2"))
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        when(repository.findById(id)).thenReturn(Optional.of(e));
        when(repository.save(any(SagaInstance.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        SagaStatusResponse resp = manager.advance(id);

        // then: avançou para o próximo
        assertThat(resp.status()).isEqualTo(SagaStatus.RUNNING.name());
        assertThat(resp.currentStep()).isEqualTo(next);

        verify(repository).findById(id);
        verify(repository).save(any(SagaInstance.class));
        // o dispatcher despacha o step ATUAL (que era INIT)
        verify(dispatcher).dispatch(eq(type), eq(first), any());
        verifyNoMoreInteractions(repository, dispatcher);
    }

    @Test
    void advance_deveCompletarQuandoNaoHaProximoStep_eNaoDespacha() {
        // given
        UUID id = UUID.randomUUID();
        String type = "USER_ONBOARDING";
        String last = SagaDefinitions.goToLast(type).orElseThrow().name();

        SagaInstance e = SagaInstance.builder()
                .id(id)
                .type(type)
                .status(SagaStatus.RUNNING)
                .currentStep(last) // já no último
                .data(mapper.nullNode())
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        when(repository.findById(id)).thenReturn(Optional.of(e));
        when(repository.save(any(SagaInstance.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        SagaStatusResponse resp = manager.advance(id);

        // then
        assertThat(resp.status()).isEqualTo(SagaStatus.COMPLETED.name());
        assertThat(resp.currentStep()).isEqualTo(last);

        verify(repository).findById(id);
        verify(repository).save(any(SagaInstance.class));
        // terminou -> não despacha novo step
        verifyNoInteractions(dispatcher);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getStatus_deveLancar404_quandoNaoEncontrar() {
        // given
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        // when / then
        assertThrows(ResponseStatusException.class, () -> manager.getStatus(id));

        verify(repository).findById(id);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(dispatcher);
    }
}

