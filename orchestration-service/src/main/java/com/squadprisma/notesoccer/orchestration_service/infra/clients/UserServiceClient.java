package com.squadprisma.notesoccer.orchestration_service.infra.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "user-service", url = "${clients.user-service.base-url:http://localhost:8081}")
public interface UserServiceClient {

    @PostMapping("/internal/users")
    void createUser(/* DTO interno futuro */);
}
