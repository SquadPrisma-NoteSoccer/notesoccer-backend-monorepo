package com.squadprisma.notesoccer.orchestration_service.domain.port;

public class SagaStepFailedException extends RuntimeException{
    public SagaStepFailedException(String message) {
        super(message);
    }
    public SagaStepFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
