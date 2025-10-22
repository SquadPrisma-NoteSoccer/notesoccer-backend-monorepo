package com.squadprisma.notesoccer.orchestration_service.api.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.squadprisma.notesoccer.orchestration_service.api.dto.CreateUsuarioRequest;
import com.squadprisma.notesoccer.orchestration_service.api.dto.UsuarioResponse;
import com.squadprisma.notesoccer.orchestration_service.api.exceptions.ApiExceptionHandler;
import com.squadprisma.notesoccer.orchestration_service.application.service.UserOrchestrationService;
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

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OrchestrationUsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ApiExceptionHandler.class)
@org.springframework.test.context.ActiveProfiles("test")
public class OrchestrationUsuarioControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper om;

    @MockBean
    UserOrchestrationService service;

    @Test
    void post_criar_usuario_201() throws Exception {
        var req = new CreateUsuarioRequest("Leonardo","leonardo@exemplo.com","12345678","Leo","+55 74 99999-9999");
        var resp = new UsuarioResponse(UUID.randomUUID(),"Leonardo","leonardo@exemplo.com","Leo","+55 74 99999-9999");

        Mockito.when(service.criarUsuario(any())).thenReturn(resp);

        mvc.perform(post("/api/v1/orquestrador/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.email").value("leonardo@exemplo.com"));
    }

    @Test
    void post_criar_usuario_400_validacao() throws Exception {
        // email inválido, senha curta, apelido vazio, whatsapp curto
        var req = new CreateUsuarioRequest("L", "email-invalido", "123", "", "123");

        mvc.perform(post("/api/v1/orquestrador/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void post_criar_usuario_409_mapeado_por_service() throws Exception {
        var req = new CreateUsuarioRequest(
                "Leonardo",                      // min 3
                "existente@exemplo.com",         // email válido
                "12345678",                      // min 8
                "Leo",                           // min 3
                "+55 11 99999-9999"              // 8..30 (este cumpre)
        );

        Mockito.when(service.criarUsuario(any()))
                .thenThrow(new ResponseStatusException(CONFLICT, "EMAIL_ALREADY_EXISTS"));

        mvc.perform(post("/api/v1/orquestrador/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("EMAIL_ALREADY_EXISTS")));
    }
}
