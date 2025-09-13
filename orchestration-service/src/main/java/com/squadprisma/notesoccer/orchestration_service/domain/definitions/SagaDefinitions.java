package com.squadprisma.notesoccer.orchestration_service.domain.definitions;

import com.squadprisma.notesoccer.orchestration_service.domain.enumtype.SagaStep;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SagaDefinitions {

    private static final Map<String, List<SagaStep>> sagaFlows = new LinkedHashMap<>();

    static {
        sagaFlows.put("USER_ONBOARDING", List.of(
                SagaStep.INIT,
                SagaStep.VALIDATE_INPUT,
                SagaStep.CALL_USER_SERVICE,
                SagaStep.CALL_NOTIFICATION,
                SagaStep.DONE
        ));

        sagaFlows.put("MATCH_CREATION", List.of(
                SagaStep.INIT,
                SagaStep.VALIDATE_INPUT,
                SagaStep.CALL_TEAM_SERVICE,
                SagaStep.CALL_MATCH_SERVICE,
                SagaStep.CALL_NOTIFICATION,
                SagaStep.DONE
        ));
    }

    private SagaDefinitions() {}

    public static Optional<SagaStep> firstStep(String sagaType) {
        var list = sagaFlows.get(sagaType);
        return (list == null || list.isEmpty()) ? Optional.empty() : Optional.of(list.get(0));
    }

    public static Optional<SagaStep> nextStep(String sagaType, String currentStep) {
        var list = sagaFlows.get(sagaType);
        if (list == null || list.isEmpty()) return Optional.empty();
        if (currentStep == null) return Optional.of(list.get(0));

        int i = -1;
        for (int idx = 0; idx < list.size(); idx++) {
            if (list.get(idx).name().equals(currentStep)) {
                i = idx; break;
            }
        }
        if (i < 0 || i + 1 >= list.size()) return Optional.empty();
        return Optional.of(list.get(i + 1));
    }

    public static boolean isLast(String sagaType, String stepName) {
        var list = sagaFlows.get(sagaType);
        if (list == null || list.isEmpty()) return true;
        return list.get(list.size() - 1).name().equals(stepName);
    }

    public static Optional<SagaStep> goToLast(String sagaType){
        var list = sagaFlows.get(sagaType);
        if (list == null || list.isEmpty()) return Optional.empty();

        return Optional.of(list.getLast());
    }
}
