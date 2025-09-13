package com.squadprisma.notesoccer.orchestration_service.domain.enumtype;

public enum SagaStep {
    INIT,                // Saga criada
    VALIDATE_INPUT,      // Validação inicial
    CALL_USER_SERVICE,   // Chamada ao user-service
    CALL_TEAM_SERVICE,   // Chamada ao team-service
    CALL_MATCH_SERVICE,  // Chamada ao match-service
    CALL_NOTIFICATION,   // Dispara notificação
    DONE,                // Concluída com sucesso
    ROLLBACK,            // Iniciou compensação
    ERROR                // Erro crítico
}
