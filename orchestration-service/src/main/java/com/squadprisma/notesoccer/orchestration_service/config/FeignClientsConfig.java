package com.squadprisma.notesoccer.orchestration_service.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!test") // não habilita Feign nos testes
@Configuration
@EnableFeignClients(basePackages = "com.squadprisma.notesoccer.orchestration_service")
public class FeignClientsConfig {
}
