package com.squadprisma.notesoccer.orchestration_service.api.controller;

import com.squadprisma.notesoccer.orchestration_service.api.dto.SagaStatusResponse;
import com.squadprisma.notesoccer.orchestration_service.api.dto.StartSagaRequest;
import com.squadprisma.notesoccer.orchestration_service.application.service.SagaManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

//@Tag(name = "Sagas")
@RestController
@RequestMapping("/sagas")
public class SagaStatusController {

    private final SagaManager manager;

    public SagaStatusController(SagaManager manager){
        this.manager = manager;
    }

//    @Operation(summary = "Consulta o status de uma saga")
    @GetMapping("/{id}")
    public ResponseEntity<SagaStatusResponse> get(@PathVariable(name = "id") UUID id){
        return ResponseEntity.ok(manager.getStatus(id));
    }

//    @Operation(summary = "Inicia uma saga")
    @PostMapping
    public ResponseEntity<SagaStatusResponse> start(@RequestBody StartSagaRequest req) {
        return ResponseEntity.ok(manager.start(req));
    }

//    @Operation(summary = "Avança a saga para o próximo passo")
    @PostMapping("/{id}/advance")
    public ResponseEntity<SagaStatusResponse> advance(@PathVariable UUID id) {
        return ResponseEntity.ok(manager.advance(id));
    }
}
