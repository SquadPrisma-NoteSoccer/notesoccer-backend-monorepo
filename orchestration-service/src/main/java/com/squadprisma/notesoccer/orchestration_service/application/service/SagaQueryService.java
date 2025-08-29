package com.squadprisma.notesoccer.orchestration_service.application.service;

import com.squadprisma.notesoccer.orchestration_service.api.dto.SagaStatusResponse;
import com.squadprisma.notesoccer.orchestration_service.domain.entity.SagaInstance;
import com.squadprisma.notesoccer.orchestration_service.repository.SagaInstanceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class SagaQueryService {

    private final SagaInstanceRepository repository;

    public SagaQueryService(SagaInstanceRepository repository){
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public SagaStatusResponse getStatus(UUID id){
        SagaInstance e = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Saga não encontrada: " + id));
        return new SagaStatusResponse(
                e.getId(),
                e.getType(),
                e.getStatus().name(),
                e.getCurrentStep(),
                e.getData(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
