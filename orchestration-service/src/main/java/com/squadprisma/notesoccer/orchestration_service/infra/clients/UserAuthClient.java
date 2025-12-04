package com.squadprisma.notesoccer.orchestration_service.infra.clients;

import com.squadprisma.notesoccer.orchestration_service.api.dto.LoginRequest;
import com.squadprisma.notesoccer.orchestration_service.api.dto.LoginResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "user-auth-service",
        url = "${external.user-service.base-url}"
)
public interface UserAuthClient {

    @PostMapping("/auth/login")
    LoginResponse login(LoginRequest request);

}
