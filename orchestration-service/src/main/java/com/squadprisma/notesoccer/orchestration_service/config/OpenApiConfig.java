package com.squadprisma.notesoccer.orchestration_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI(){
        Server httpServer = new Server()
                .url("https://notesoccer.lavicestas.com.br")
                .description("Notesoccer via Cloudflare Tunnel");

        return new OpenAPI().servers(List.of(httpServer));
    }
}
