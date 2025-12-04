package com.squadprisma.notesoccer.orchestration_service;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@OpenAPIDefinition(
        info = @Info(
                title = "NoteSoccer Orchestration API",
                version = "v0",
                description = "Orquestrador do NoteSoccer"
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "Dev local"
                ),
                @Server(
                        url = "https://notesoccer.lavicestas.com.br",
                        description = "Notesoccer via Cloudflare Tunnel"
                )
        }
)
@SpringBootApplication
public class OrchestrationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrchestrationServiceApplication.class, args);
    }

}
