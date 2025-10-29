package com.squadprisma.notesoccer.orchestration_service.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squadprisma.notesoccer.orchestration_service.api.dto.*;
import com.squadprisma.notesoccer.orchestration_service.api.exceptions.GlobalExceptionHandler;
import com.squadprisma.notesoccer.orchestration_service.application.service.LeagueOrchestrationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OrchestrationLigaController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@org.springframework.test.context.ActiveProfiles("test")
public class OrchestrationLigaControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper om;

    @MockBean
    LeagueOrchestrationService service;

    @Test
    void post_criar_liga_201() throws Exception {
        var resp = new LigaResponse(UUID.randomUUID(), "Liga Zona Norte", Instant.now());
        Mockito.when(service.criarLiga(any())).thenReturn(resp);

        var body = new CreateLigaRequest("Liga Zona Norte", UUID.randomUUID());

        mvc.perform(post("/api/v1/orquestrador/ligas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.nome").value("Liga Zona Norte"));
    }

    @Test
    void post_criar_liga_400_validacao_nome_em_branco() throws Exception {
        var body = new CreateLigaRequest("   ", UUID.randomUUID()); // @NotBlank em DTO

        mvc.perform(post("/api/v1/orquestrador/ligas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void post_criar_liga_409_mapeado_por_service() throws Exception {
        Mockito.when(service.criarLiga(any()))
                .thenThrow(new ResponseStatusException(CONFLICT, "LEAGUE_ALREADY_EXISTS"));

        var body = new CreateLigaRequest("Liga X", UUID.randomUUID());

        mvc.perform(post("/api/v1/orquestrador/ligas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("LEAGUE_ALREADY_EXISTS")));
    }

    @Test
    void post_criar_time_201_forca_ligaId_do_path() throws Exception {
        var ligaId = UUID.randomUUID();
        var resp = new TimeResponse(UUID.randomUUID(), ligaId, "Atlético Jardim", Instant.now());
        Mockito.when(service.criarTime(eq(ligaId), anyString())).thenReturn(resp);

        // Mesmo que o body tenha outro leagueId, controller usa o do path
        var body = new CreateTimeRequest(UUID.randomUUID(), "Atlético Jardim");

        mvc.perform(post("/api/v1/orquestrador/ligas/{ligaId}/times", ligaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ligaId").value(ligaId.toString()))
                .andExpect(jsonPath("$.nome").value("Atlético Jardim"));
    }

    @Test
    void get_listar_times_200() throws Exception {
        var ligaId = UUID.randomUUID();
        var t1 = new TimeResponse(UUID.randomUUID(), ligaId, "Time A", Instant.now());
        var t2 = new TimeResponse(UUID.randomUUID(), ligaId, "Time B", Instant.now());
        var page = new PageResponse<>(List.of(t1, t2), 0, 20, 2, 1);

        Mockito.when(service.listarTimes(ligaId, 0, 20)).thenReturn(page);

        mvc.perform(get("/api/v1/orquestrador/ligas/{ligaId}/times", ligaId)
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void get_contar_times_200() throws Exception {
        var ligaId = UUID.randomUUID();
        var resp = new TimeCountResponse(ligaId, 7);
        Mockito.when(service.contarTimes(ligaId)).thenReturn(resp);

        mvc.perform(get("/api/v1/orquestrador/ligas/{ligaId}/times/count", ligaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ligaId").value(ligaId.toString()))
                .andExpect(jsonPath("$.count").value(7));
    }
}
