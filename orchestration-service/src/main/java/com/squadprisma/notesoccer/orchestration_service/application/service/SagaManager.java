package com.squadprisma.notesoccer.orchestration_service.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.squadprisma.notesoccer.orchestration_service.api.dto.SagaStatusResponse;
import com.squadprisma.notesoccer.orchestration_service.api.dto.StartSagaRequest;
import com.squadprisma.notesoccer.orchestration_service.domain.definitions.SagaDefinitions;
import com.squadprisma.notesoccer.orchestration_service.domain.entity.SagaInstance;
import com.squadprisma.notesoccer.orchestration_service.domain.enumtype.SagaStatus;
import com.squadprisma.notesoccer.orchestration_service.domain.port.SagaDispatcher;
import com.squadprisma.notesoccer.orchestration_service.repository.SagaInstanceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class SagaManager {

    private final SagaInstanceRepository repository;
    private final SagaDispatcher dispatcher;

    public SagaManager(SagaInstanceRepository repository, SagaDispatcher dispatcher) {
        this.repository = repository;
        this.dispatcher = dispatcher;
    }

    @Transactional(readOnly = true)
    public SagaStatusResponse getStatus(UUID id) {
        SagaInstance e = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Saga não encontrada: " + id));

        return toDto(e);
    }

    /**
     * Inicia a saga: persiste RUNNING + primeiro step e DESPACHA o primeiro step,
     * mas NÃO AVANÇA o currentStep no banco.
     */
    @Transactional
    public SagaStatusResponse start(StartSagaRequest req) {
        String type = req.type();

        var first = SagaDefinitions.firstStep(type)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Nenhum passo definido para a saga type=" + type));

        SagaInstance e = SagaInstance.builder()
                .type(type)
                .status(SagaStatus.RUNNING)
                .currentStep(first.name())
                .data(req.payload()) // JsonNode
                .build();

        e = repository.save(e);

        // dispara execução do primeiro passo (sem avançar estado no banco)
        dispatcher.dispatch(type, first.name(), e.getData());

        return toDto(e);
    }

    @Transactional
    public SagaStatusResponse advance(UUID id) {
        SagaInstance e = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Saga não encontrada: " + id));

        if (e.getStatus() != SagaStatus.RUNNING && e.getStatus() != SagaStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Saga não está em execução.");
        }

        final String current = e.getCurrentStep();

        // 1) calcula o próximo ANTES de despachar
        var maybeNext = SagaDefinitions.nextStep(e.getType(), current);

        if (maybeNext.isEmpty()) {
            // Já está no último step -> NÃO despacha; apenas completa
            e.setStatus(SagaStatus.COMPLETED);
            e = repository.save(e);
            return new SagaStatusResponse(
                    e.getId(), e.getType(), e.getStatus().name(), e.getCurrentStep(),
                    e.getData(), e.getCreatedAt(), e.getUpdatedAt()
            );
        }

        // 2) há próximo step -> despacha o step ATUAL e avança
        dispatcher.dispatch(e.getType(), current, e.getData());

        e.setCurrentStep(maybeNext.get().name());
        e.setStatus(SagaStatus.RUNNING);

        e = repository.save(e);
        return new SagaStatusResponse(
                e.getId(), e.getType(), e.getStatus().name(), e.getCurrentStep(),
                e.getData(), e.getCreatedAt(), e.getUpdatedAt()
        );
    }

    private SagaStatusResponse toDto(SagaInstance e) {
        JsonNode data = e.getData();
        return new SagaStatusResponse(
                e.getId(),
                e.getType(),
                e.getStatus().name(),
                e.getCurrentStep(),
                data,
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
