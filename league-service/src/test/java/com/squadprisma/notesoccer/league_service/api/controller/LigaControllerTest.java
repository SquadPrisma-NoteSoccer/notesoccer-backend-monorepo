package com.squadprisma.notesoccer.league_service.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squadprisma.notesoccer.league_service.api.dto.LigaCreateRequest;
import com.squadprisma.notesoccer.league_service.domain.entity.Liga;
import com.squadprisma.notesoccer.league_service.service.LigaService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = LigaController.class)
public class LigaControllerTest extends BaseControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper om;

    @MockBean
    LigaService service;

    @Test
    void post_league_201() throws Exception {
        Liga l = new Liga();
        l.setNome("Liga X");

        Mockito.when(service.create(Mockito.any())).thenReturn(l);

        var body = new LigaCreateRequest("Liga X");

        mvc.perform(post("/api/v1/ligas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Liga X"));
    }

}
