package com.squadprisma.notesoccer.league_service.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squadprisma.notesoccer.league_service.api.dto.TimeCreateRequest;
import com.squadprisma.notesoccer.league_service.domain.entity.Liga;
import com.squadprisma.notesoccer.league_service.domain.entity.Time;
import com.squadprisma.notesoccer.league_service.domain.exception.BadRequestException;
import com.squadprisma.notesoccer.league_service.domain.exception.ConflictException;
import com.squadprisma.notesoccer.league_service.domain.exception.NotFoundException;
import com.squadprisma.notesoccer.league_service.service.TimeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TimeController.class)
public class TimeControllerTest extends BaseControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper om;

    @MockBean
    TimeService service;

    @Test
    void post_team_201() throws Exception {
        var ligaId = UUID.randomUUID();

        Time saved = new Time();
        Liga l = new Liga();
        l.setId(ligaId);
        saved.setLiga(l);
        saved.setNome("Atlético Jardim");

        when(service.create(any())).thenReturn(saved);

        var body = new TimeCreateRequest(ligaId, "Atlético Jardim");

        mvc.perform(post("/api/v1/times")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Atlético Jardim"))
                .andExpect(jsonPath("$.ligaId").value(ligaId.toString()));
    }

    @Test
    void post_team_400_validacao_regex() throws Exception {
        var ligaId = UUID.randomUUID();

        mvc.perform(post("/api/v1/times")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {"leagueId":"%s","name":"Time@Errado"}
            """.formatted(ligaId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR")); // conforme seu handler
    }

    @Test
    void post_team_409_duplicado() throws Exception {
        var ligaId = UUID.randomUUID();
        when(service.create(any()))
                .thenThrow(new ConflictException("TEAM_ALREADY_EXISTS"));

        var body = new TimeCreateRequest(ligaId, "Time X");

        mvc.perform(post("/api/v1/times")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("TEAM_ALREADY_EXISTS"));
    }

    @Test
    void delete_team_204() throws Exception {
        UUID ligaId = UUID.randomUUID();
        UUID timeId = UUID.randomUUID();

        mvc.perform(delete("/api/v1/times/{timeId}", timeId)
                        .param("ligaId", ligaId.toString()))
                .andExpect(status().isNoContent());

        verify(service, times(1)).delete(ligaId, timeId);
    }

    @Test
    void delete_team_404_not_found() throws Exception {
        UUID ligaId = UUID.randomUUID();
        UUID timeId = UUID.randomUUID();

        doThrow(new NotFoundException("TEAM_NOT_FOUND"))
                .when(service).delete(ligaId, timeId);

        mvc.perform(delete("/api/v1/times/{timeId}", timeId)
                        .param("ligaId", ligaId.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TEAM_NOT_FOUND"));
    }

    @Test
    void delete_team_400_time_nao_pertence_a_liga() throws Exception {
        UUID ligaId = UUID.randomUUID();
        UUID timeId = UUID.randomUUID();

        doThrow(new BadRequestException("TEAM_NOT_IN_LEAGUE"))
                .when(service).delete(ligaId, timeId);

        mvc.perform(delete("/api/v1/times/{timeId}", timeId)
                        .param("ligaId", ligaId.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("TEAM_NOT_IN_LEAGUE"));
    }


}
