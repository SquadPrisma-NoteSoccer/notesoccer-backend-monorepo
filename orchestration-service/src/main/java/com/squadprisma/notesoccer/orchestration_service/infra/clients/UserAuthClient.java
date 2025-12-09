package com.squadprisma.notesoccer.orchestration_service.infra.clients;

import com.squadprisma.notesoccer.orchestration_service.api.dto.CadastroUsuarioRequest;
import com.squadprisma.notesoccer.orchestration_service.api.dto.LoginRequest;
import com.squadprisma.notesoccer.orchestration_service.api.dto.LoginResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "user-auth-service",
        url = "${external.user-service.base-url}"
)
public interface UserAuthClient {

    @PostMapping("/api/v1/auth/login")
    LoginResponse login(@RequestBody LoginRequest request);

    @PostMapping("/api/v1/auth/signup")
    LoginResponse signup(@RequestBody CadastroUsuarioRequest request);

}
