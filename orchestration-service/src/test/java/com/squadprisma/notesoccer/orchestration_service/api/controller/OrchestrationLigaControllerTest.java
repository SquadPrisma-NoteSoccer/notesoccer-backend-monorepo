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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OrchestrationLigaController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
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

    @Test
    void delete_league_204() throws Exception {
        UUID ligaId = UUID.randomUUID();

        mvc.perform(delete("/api/v1/orquestrador/ligas/{ligaId}", ligaId))
                .andExpect(status().isNoContent());

        verify(service).deletarLiga(ligaId);
    }

    @Test
    void delete_league_404_not_found() throws Exception {
        UUID ligaId = UUID.randomUUID();

        Mockito.doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "LEAGUE_NOT_FOUND"))
                .when(service)
                .deletarLiga(ligaId);

        mvc.perform(delete("/api/v1/orquestrador/ligas/{ligaId}", ligaId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("LEAGUE_NOT_FOUND"));
    }

    @Test
    void get_leagues_by_user_200() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID ligaId = UUID.randomUUID();

        LigaResponse liga = new LigaResponse(
                ligaId,
                "Liga do Leo",
                Instant.parse("2025-01-01T10:00:00Z")
        );

        PageResponse<LigaResponse> page = new PageResponse<>(
                List.of(liga),
                0,
                20,
                1L,
                1
        );

        Mockito.when(service.listarLigasPorUsuario(eq(userId), anyInt(), anyInt()))
                .thenReturn(page);

        mvc.perform(get("/api/v1/orquestrador/ligas")
                        .param("userId", userId.toString())
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(ligaId.toString()))
                .andExpect(jsonPath("$.content[0].nome").value("Liga do Leo"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
    @Test
    void delete_time_204() throws Exception {
        UUID ligaId = UUID.randomUUID();
        UUID timeId = UUID.randomUUID();

        mvc.perform(delete("/api/v1/orquestrador/ligas/{ligaId}/times/{timeId}", ligaId, timeId))
                .andExpect(status().isNoContent());

        verify(service).deletarTime(ligaId, timeId);
    }
    @Test
    void delete_time_404_team_not_found() throws Exception {
        UUID ligaId = UUID.randomUUID();
        UUID timeId = UUID.randomUUID();

        Mockito.doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "TEAM_NOT_FOUND"))
                .when(service)
                .deletarTime(ligaId, timeId);

        mvc.perform(delete("/api/v1/orquestrador/ligas/{ligaId}/times/{timeId}", ligaId, timeId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TEAM_NOT_FOUND"));
    }
}
