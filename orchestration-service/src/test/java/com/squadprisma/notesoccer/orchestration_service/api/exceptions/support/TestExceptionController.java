package com.squadprisma.notesoccer.orchestration_service.api.exceptions.support;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/test-ex")
@Validated
public class TestExceptionController {

    @GetMapping("/conflict")
    public String conflict() {
        throw new com.squadprisma.notesoccer.orchestration_service.domain.exception.ConflictException("LEAGUE_ALREADY_EXISTS");
    }

    @GetMapping("/illegal")
    public String illegal() {
        throw new IllegalArgumentException("bad_request_reason");
    }

    @PostMapping("/bean")
    public String bean(@RequestBody @jakarta.validation.Valid CreateDto body) {
        return "ok";
    }

    @GetMapping("/constraint")
    public String constraint(@RequestParam @Min(value = 1, message = "must be >= 1") int size) {
        return "ok:" + size;
    }

    @GetMapping("/rse-502")
    public String rse502() {
        throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "league-service error: 502");
    }

    @GetMapping("/rse-504")
    public String rse504() {
        throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "league-service timeout");
    }

    @GetMapping("/unknown")
    public String unknown() {
        throw new RuntimeException("boom");
    }

    public static class CreateDto {
        @NotBlank(message = "name is required")
        public String name;
    }
}
