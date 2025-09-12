package com.squadprisma.notesoccer.orchestration_service.domain.port;


import com.fasterxml.jackson.databind.JsonNode;
import com.squadprisma.notesoccer.orchestration_service.domain.entity.SagaInstance;
import com.squadprisma.notesoccer.orchestration_service.domain.enumtype.SagaStep;

public interface SagaDispatcher {
    void dispatch(String sagaType, String sagaStep, JsonNode data) throws SagaStepFailedException;
}
