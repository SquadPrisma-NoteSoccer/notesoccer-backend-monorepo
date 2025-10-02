package com.squadprisma.notesoccer.orchestration_service.infra.dispatcher;

import com.squadprisma.notesoccer.orchestration_service.domain.port.SagaDispatcher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SagaDispatcherConfig {

    @Bean
    @ConditionalOnProperty(name = "saga.enabled", havingValue = "true")
    SagaDispatcher http(HttpSagaDispatcher d){
        return d;
    }

    @Bean
    @ConditionalOnProperty(name="saga.enabled", havingValue="false", matchIfMissing = true)
    SagaDispatcher noop(NoopSagaDispatcher d){
        return d;
    }
}

