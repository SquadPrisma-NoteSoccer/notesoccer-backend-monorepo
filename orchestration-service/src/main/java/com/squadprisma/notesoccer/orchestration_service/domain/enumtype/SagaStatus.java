package com.squadprisma.notesoccer.orchestration_service.domain.enumtype;

public enum SagaStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    COMPENSATING,
    FAILED,
    CANCELLED
}
