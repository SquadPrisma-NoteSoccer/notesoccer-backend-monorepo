package com.squadprisma.notesoccer.orchestration_service.config;

import com.squadprisma.notesoccer.orchestration_service.infra.DownstreamHttpException;
import feign.Util;
import feign.codec.ErrorDecoder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

@Profile("!test") // não habilita Feign nos testes
@Configuration
@EnableFeignClients(basePackages = "com.squadprisma.notesoccer.orchestration_service")
public class FeignClientsConfig {

    @Bean
    public ErrorDecoder downstreamErrorDecoder() {
        return (methodKey, response) -> {
            String body = "";
            if (response.body() != null) {
                try {
                    body = Util.toString(response.body().asReader(StandardCharsets.UTF_8));
                } catch (IOException ignored) {}
            }

            HttpStatus status = HttpStatus.resolve(response.status());
            if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;

            // Aqui está o ajuste 👇
            Map<String, Collection<String>> headers = response.headers();

            String service = methodKey.contains("#")
                    ? methodKey.substring(0, methodKey.indexOf('#'))
                    : methodKey;

            return new DownstreamHttpException(status, body, headers, service);
        };
    }
}
