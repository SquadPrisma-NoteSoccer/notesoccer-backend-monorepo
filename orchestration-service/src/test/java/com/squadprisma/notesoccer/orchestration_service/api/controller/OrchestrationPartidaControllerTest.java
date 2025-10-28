package com.squadprisma.notesoccer.orchestration_service.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squadprisma.notesoccer.orchestration_service.api.dto.CreatePartidaRequest;
import com.squadprisma.notesoccer.orchestration_service.api.dto.PartidaResponse;
import com.squadprisma.notesoccer.orchestration_service.api.exceptions.GlobalExceptionHandler;
import com.squadprisma.notesoccer.orchestration_service.application.service.MatchOrchestrationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OrchestrationPartidaController.class)
@Import(GlobalExceptionHandler.class)
public class OrchestrationPartidaControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper om;

    @MockBean
    MatchOrchestrationService service;

    @Test
    void deveCriarPartida_comSucesso() throws Exception {
        var req = new CreatePartidaRequest(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                OffsetDateTime.now().plusDays(1),
                OffsetDateTime.now().plusDays(1).plusHours(2),
                "Quadra A", null);

        var resp = new PartidaResponse(
                UUID.randomUUID(), UUID.randomUUID(),
                req.casaTimeId(), req.visitanteTimeId(),
                req.startAt(), req.endAt(), req.local(), null, "AGENDADA"
        );

        Mockito.when(service.criar(any())).thenReturn(resp);

        mvc.perform(post("/api/v1/orquestrador/partidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("AGENDADA"));
    }

    @Test
    void deveFalhar_validacaoQuandoTimesIguais() throws Exception {
        var id = UUID.randomUUID();
        var req = new CreatePartidaRequest(
                UUID.randomUUID(), id, id,
                OffsetDateTime.now().plusDays(1),
                OffsetDateTime.now().plusDays(1).plusHours(1),"Quadra A", null
        );

        Mockito.doThrow(new IllegalArgumentException("Times devem ser distintos"))
                .when(service).criar(Mockito.any());

        mvc.perform(post("/api/v1/orquestrador/partidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

}
