package com.squadprisma.notesoccer.orchestration_service.infra.dispatcher;

import com.fasterxml.jackson.databind.JsonNode;
import com.squadprisma.notesoccer.orchestration_service.domain.port.SagaDispatcher;
import com.squadprisma.notesoccer.orchestration_service.domain.port.SagaStepFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Primary
public class NoopSagaDispatcher implements SagaDispatcher {

    @Override
    public void dispatch(String sagaType, String sagaStep, JsonNode data) throws SagaStepFailedException {
        log.info("[Dispatcher/NOOP] type={} step={} payload={}", sagaType, sagaStep, data);

    }
}
