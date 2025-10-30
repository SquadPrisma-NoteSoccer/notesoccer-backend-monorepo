package com.squadprisma.notesoccer.match_service.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squadprisma.notesoccer.match_service.api.dto.PartidaRequest;
import com.squadprisma.notesoccer.match_service.api.dto.PartidaResponse;
import com.squadprisma.notesoccer.match_service.domain.entity.Partida;
import com.squadprisma.notesoccer.match_service.domain.enums.PartidaStatus;
import com.squadprisma.notesoccer.match_service.service.PartidaService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(PartidaController.class)
public class PartidaControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper om;

    @MockBean
    PartidaService service;

    private Partida mockPartida() {
        Partida p = Partida.builder()
                .id(UUID.randomUUID())
                .ligaId(UUID.randomUUID())
                .casaTimeId(UUID.randomUUID())
                .visitanteTimeId(UUID.randomUUID())
                .startAt(OffsetDateTime.parse("2025-07-22T09:00:00-03:00"))
                .endAt(OffsetDateTime.parse("2025-07-22T10:30:00-03:00"))
                .local("Quadra A")
                .notas("Levar coletes")
                .status(PartidaStatus.AGENDADA)
                .build();
        return p;
    }

    @Test
    void create_returns_200_with_body() throws Exception {
        Partida saved = mockPartida();
        Mockito.when(service.criar(any(PartidaRequest.class))).thenReturn(toDto(saved));

        PartidaRequest req = new PartidaRequest(
                saved.getLigaId(), saved.getCasaTimeId(), saved.getVisitanteTimeId(),
                saved.getStartAt(), saved.getEndAt(), "Quadra A", "Levar coletes");

        mvc.perform(post("/api/v1/partidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.status", is("AGENDADA")))
                .andExpect(jsonPath("$.local", is("Quadra A")));
    }

    @Test
    void create_returns_409_on_conflict() throws Exception {
        Mockito.when(service.criar(any(PartidaRequest.class))).thenThrow(new IllegalStateException("Conflito de agenda"));

        PartidaRequest req = new PartidaRequest(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                OffsetDateTime.parse("2025-07-22T09:00:00-03:00"),
                OffsetDateTime.parse("2025-07-22T10:30:00-03:00"),
                null, null);

        mvc.perform(post("/api/v1/partidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test
    void create_returns_400_on_business_validation() throws Exception {
        Mockito.when(service.criar(any(PartidaRequest.class))).thenThrow(new IllegalArgumentException("Horário inválido"));

        PartidaRequest req = new PartidaRequest(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                OffsetDateTime.parse("2025-07-22T10:30:00-03:00"),
                OffsetDateTime.parse("2025-07-22T09:00:00-03:00"),
                null, null);

        mvc.perform(post("/api/v1/partidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void calendar_returns_list() throws Exception {
        Partida p = mockPartida();
        Mockito.when(service.calendario(any(), any(), any())).thenReturn(List.of(toDto(p)));

        UUID ligaId = p.getLigaId();
        String from = "2025-07-01T00:00:00-03:00";
        String to   = "2025-07-31T23:59:59-03:00";

        mvc.perform(get("/api/v1/partidas/calendario")
                        .param("ligaId", ligaId.toString())
                        .param("from", from)
                        .param("to", to))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].ligaId", is(ligaId.toString())))
                .andExpect(jsonPath("$[0].status", is("AGENDADA")));
    }

    private PartidaResponse toDto(Partida p){
        PartidaResponse response = new PartidaResponse(
                p.getId(), p.getLigaId(), p.getCasaTimeId(), p.getVisitanteTimeId(),
                p.getStartAt(), p.getEndAt(), p.getLocal(), p.getNotas(),
                p.getCreatedAt(), p.getUpdatedAt(), p.getStatus()
        );
        return response;
    }


}
