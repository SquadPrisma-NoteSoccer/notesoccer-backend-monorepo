package com.squadprisma.notesoccer.orchestration_service.api.exceptions;

import com.squadprisma.notesoccer.orchestration_service.api.exceptions.support.TestExceptionController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TestExceptionController.class)
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

    @Autowired MockMvc mvc;

    @Test @DisplayName("409 | ConflictException → body padronizado")
    void conflict() throws Exception {
        mvc.perform(get("/test-ex/conflict"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.code").value("LEAGUE_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value("LEAGUE_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.path").value("/test-ex/conflict"))
                .andExpect(jsonPath("$.timestamp", not(emptyString())));
    }

    @Test @DisplayName("400 | IllegalArgumentException → body padronizado")
    void illegalArgument() throws Exception {
        mvc.perform(get("/test-ex/illegal"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.code").value("bad_request_reason"))
                .andExpect(jsonPath("$.message").value("bad_request_reason"))
                .andExpect(jsonPath("$.path").value("/test-ex/illegal"));
    }

    @Test @DisplayName("400 | MethodArgumentNotValidException → envelope com errors[]")
    void beanValidationBody() throws Exception {
        mvc.perform(post("/test-ex/bean")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Erro de validação nos campos enviados."))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].field").value("name"))
                .andExpect(jsonPath("$.errors[0].message").value("name is required"))
                .andExpect(jsonPath("$.path").value("/test-ex/bean"));
    }

    @Test @DisplayName("400 | ConstraintViolationException → envelope com errors[]")
    void constraintViolation() throws Exception {
        mvc.perform(get("/test-ex/constraint").param("size", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.code").value("CONSTRAINT_VIOLATION"))
                .andExpect(jsonPath("$.message").value("Parâmetros inválidos."))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].param", containsString("size")))
                .andExpect(jsonPath("$.errors[0].message").value("must be >= 1"))
                .andExpect(jsonPath("$.path").value("/test-ex/constraint"));
    }

    @Test @DisplayName("502 | ResponseStatusException(BAD_GATEWAY)")
    void responseStatus502() throws Exception {
        mvc.perform(get("/test-ex/rse-502"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(502))
                .andExpect(jsonPath("$.error").value("Bad Gateway"))
                .andExpect(jsonPath("$.code").value("league-service error: 502"))
                .andExpect(jsonPath("$.message").value("league-service error: 502"))
                .andExpect(jsonPath("$.path").value("/test-ex/rse-502"));
    }

    @Test @DisplayName("504 | ResponseStatusException(GATEWAY_TIMEOUT)")
    void responseStatus504() throws Exception {
        mvc.perform(get("/test-ex/rse-504"))
                .andExpect(status().isGatewayTimeout())
                .andExpect(jsonPath("$.status").value(504))
                .andExpect(jsonPath("$.error").value("Gateway Timeout"))
                .andExpect(jsonPath("$.code").value("league-service timeout"))
                .andExpect(jsonPath("$.message").value("league-service timeout"))
                .andExpect(jsonPath("$.path").value("/test-ex/rse-504"));
    }

    @Test @DisplayName("500 | Fallback Exception → UNEXPECTED_ERROR")
    void fallback500() throws Exception {
        mvc.perform(get("/test-ex/unknown"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.code").value("UNEXPECTED_ERROR"))
                .andExpect(jsonPath("$.message").value("Ocorreu um erro inesperado."))
                .andExpect(jsonPath("$.path").value("/test-ex/unknown"));
    }
}