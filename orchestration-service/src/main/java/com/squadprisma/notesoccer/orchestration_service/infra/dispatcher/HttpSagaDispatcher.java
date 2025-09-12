package com.squadprisma.notesoccer.orchestration_service.infra.dispatcher;

import com.fasterxml.jackson.databind.JsonNode;
import com.squadprisma.notesoccer.orchestration_service.domain.enumtype.SagaStep;
import com.squadprisma.notesoccer.orchestration_service.domain.port.SagaDispatcher;
import com.squadprisma.notesoccer.orchestration_service.domain.port.SagaStepFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
//@Primary
@Component
@RequiredArgsConstructor
public class HttpSagaDispatcher implements SagaDispatcher {

//    private final UserServiceClient userClient;

    @Override
    public void dispatch(String sagaType, String sagaStep, JsonNode data) throws SagaStepFailedException {
        SagaStep step = SagaStep.valueOf(sagaStep);

        switch (step){
            case VALIDATE_INPUT -> validateInput(data);
            case CALL_USER_SERVICE -> callUserService(data);
            case CALL_NOTIFICATION -> notifyUser(data);
            case INIT, CALL_TEAM_SERVICE, CALL_MATCH_SERVICE, DONE -> {
                log.info("[Dispatcher] step={} -> noop por enquanto", step);
            }
        }

    }

    private void validateInput(JsonNode data){
        if (data == null || data.get("userId") == null || data.get("userId").asText().isBlank()){
            throw new SagaStepFailedException("Campo obrigatório ausente: userId");
        }
    }

    private void callUserService(JsonNode data){
        String userId = data.get("userId").asText();
        try{
            // userClient.createUser(userId); // exemplo
            log.info("[Dispatcher] chamaria user-service para userId={}", userId);
        }catch (Exception e){
            throw new SagaStepFailedException("Falha ao chamar user-service", e);
        }
    }

    private void notifyUser(JsonNode data){
        // chamada ao notification-service (exemplo)
        log.info("[Dispatcher] chamaria notification-service");
    }
}
