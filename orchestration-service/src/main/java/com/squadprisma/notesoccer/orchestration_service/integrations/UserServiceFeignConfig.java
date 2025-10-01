package com.squadprisma.notesoccer.orchestration_service.integrations;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

public class UserServiceFeignConfig {

    @Bean
    RequestInterceptor correlationId(){
        return requestTemplate -> {
            // gera / propaga um X-Correlation-Id simples
            if (!requestTemplate.headers().containsKey("X-Correlation-Id")){
                requestTemplate.header("X-Correlation-Id", UUID.randomUUID().toString());
            }
        };
    }
}
