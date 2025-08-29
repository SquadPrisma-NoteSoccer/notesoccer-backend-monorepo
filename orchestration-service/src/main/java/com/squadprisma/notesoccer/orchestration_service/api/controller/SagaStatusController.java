package com.squadprisma.notesoccer.orchestration_service.api.controller;

import com.squadprisma.notesoccer.orchestration_service.api.dto.SagaStatusResponse;
import com.squadprisma.notesoccer.orchestration_service.application.service.SagaQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Sagas")
@RestController
@RequestMapping("api/v1/sagas")
public class SagaStatusController {

    private final SagaQueryService service;

    public SagaStatusController(SagaQueryService service){
        this.service = service;
    }

    @Operation(summary = "Consulta o status de uma saga")
    @GetMapping("/{id}")
    public ResponseEntity<SagaStatusResponse> get(@PathVariable(name = "id") UUID id){
        return ResponseEntity.ok(service.getStatus(id));
    }
}
