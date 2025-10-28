package com.squadprisma.notesoccer.orchestration_service.api.controller;

import com.squadprisma.notesoccer.orchestration_service.api.dto.PartidaResponse;
import com.squadprisma.notesoccer.orchestration_service.api.exceptions.GlobalExceptionHandler;
import com.squadprisma.notesoccer.orchestration_service.application.service.MatchOrchestrationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrchestrationPartidaController.class)
@Import(GlobalExceptionHandler.class)
public class OrchestrationPartidaControllerStatusTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    MatchOrchestrationService service;

    @Test
    void deveAlterarStatus_paraEmAndamento() throws Exception {
        var id = UUID.randomUUID();
        var resp = new PartidaResponse(id, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                OffsetDateTime.now().plusDays(1), OffsetDateTime.now().plusDays(1).plusHours(2),
                "Quadra A", null, "EM_ANDAMENTO");

        Mockito.when(service.alterarStatus(Mockito.eq(id), Mockito.eq("EM_ANDAMENTO")))
                .thenReturn(resp);

        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .patch("/api/v1/orquestrador/partidas/{id}/status", id)
                .param("status", "EM_ANDAMENTO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EM_ANDAMENTO"));
    }
}
