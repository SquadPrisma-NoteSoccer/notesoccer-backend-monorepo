package com.squadprisma.notesoccer.orchestration_service.infra.clients;

import com.squadprisma.notesoccer.orchestration_service.api.dto.CreateUsuarioRequest;
import com.squadprisma.notesoccer.orchestration_service.api.dto.UsuarioResponse;
import com.squadprisma.notesoccer.orchestration_service.infra.feign.FeignDownstreamConfig;
import com.squadprisma.notesoccer.orchestration_service.integrations.UserServiceFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "user-service",
        url = "${external.user-service.base-url}",
        configuration = {UserServiceFeignConfig.class, FeignDownstreamConfig.class}
)
public interface UserServiceClient {

    @PostMapping(value = "/api/v1/usuarios", consumes = "application/json")
    UsuarioResponse criar(@RequestBody CreateUsuarioRequest request);
}