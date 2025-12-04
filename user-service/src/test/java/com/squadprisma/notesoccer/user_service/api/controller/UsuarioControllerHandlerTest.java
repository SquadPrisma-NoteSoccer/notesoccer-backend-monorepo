package com.squadprisma.notesoccer.user_service.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squadprisma.notesoccer.user_service.api.dto.CadastroUsuarioDTO;
import com.squadprisma.notesoccer.user_service.api.handler.GlobalExceptionHandler;
import com.squadprisma.notesoccer.user_service.domain.entity.Usuario;
import com.squadprisma.notesoccer.user_service.domain.exception.EmailAlreadyInUseException;
import com.squadprisma.notesoccer.user_service.repository.UsuarioRepository;
import com.squadprisma.notesoccer.user_service.service.UsuarioService;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Slice test do controller com o @RestControllerAdvice ativo,
 * exercitando o contrato de erro padronizado do GlobalExceptionHandler.
 */
@WebMvcTest(controllers = UsuarioController.class)
@Import(GlobalExceptionHandler.class)
@TestPropertySource(properties = {
        // Necessário para o teste de 404 cair no handler
        "spring.mvc.throw-exception-if-no-handler-found=true",
        "spring.web.resources.add-mappings=false"
})
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerHandlerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UsuarioService service;

    @MockBean
    UsuarioRepository repository;

    private CadastroUsuarioDTO dtoValido() {
        return new CadastroUsuarioDTO(
                "Leonardo Ferraz",
                "leonardo@example.com",
                "Senha@123",
                "Leo",
                "5511998765432"
        );
    }

    @Test
    void criar_deveRetornar201_comBodyEsperado() throws Exception {
        var salvo = Usuario.builder()
                .id(UUID.randomUUID())
                .nome("Leonardo Ferraz")
                .email("leonardo@example.com")
                .apelido("Leo")
                .whatsappE164("5511998765432")
                .build();

        when(service.criar(any(CadastroUsuarioDTO.class))).thenReturn(salvo);

        val jsonPathResultMatchers = jsonPath("$.whatsappE164");
        mvc.perform(post("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dtoValido())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(salvo.getId().toString()))
                .andExpect(jsonPath("$.nome").value("Leonardo Ferraz"))
                .andExpect(jsonPath("$.email").value("leonardo@example.com"))
                .andExpect(jsonPath("$.apelido").value("Leo"))
                .andExpect(jsonPath("$.whatsapp").value("5511998765432"));
    }

    @Test
    void criar_deveRetornar400_quandoPayloadInvalido_comViolations() throws Exception {
        var invalido = new CadastroUsuarioDTO(
                "L",
                "email-invalido",
                "123",
                "!!",
                "abc"
        );

        mvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Erro de validação nos campos."))
                .andExpect(jsonPath("$.path").value("/api/v1/usuarios"))
                .andExpect(jsonPath("$.violations").isArray());
    }

    @Test
    void criar_deveRetornar409_quandoEmailDuplicado() throws Exception {
        when(service.criar(any(CadastroUsuarioDTO.class)))
                .thenThrow(new EmailAlreadyInUseException("leonardo@example.com"));

        mvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dtoValido())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.code").value("EMAIL_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value("E-mail já cadastrado."))
                .andExpect(jsonPath("$.path").value("/api/v1/usuarios"));
    }

    @Test
    void criar_deveRetornar409_quandoViolacaoIntegridadeGenerica() throws Exception {
        when(service.criar(any(CadastroUsuarioDTO.class)))
                .thenThrow(new DataIntegrityViolationException("violacao"));

        mvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dtoValido())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.code").value("DATA_INTEGRITY_VIOLATION"))
                .andExpect(jsonPath("$.message").value("Violação de integridade de dados."))
                .andExpect(jsonPath("$.path").value("/api/v1/usuarios"));
    }

    @Test
    void criar_deveRetornar400_quandoJsonMalFormatado() throws Exception {
        String jsonQuebrado = "{ \"nome\": \"Leonardo\", \"email\": \"leo@example.com\", \"senha\": 123 ";

        mvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonQuebrado))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("MALFORMED_JSON"))
                .andExpect(jsonPath("$.message").value("Corpo da requisição inválido ou mal formatado."))
                .andExpect(jsonPath("$.path").value("/api/v1/usuarios"));
    }

    @Test
    void deveRetornar405_quandoMetodoNaoSuportado() throws Exception {
        mvc.perform(get("/api/v1/usuarios"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(jsonPath("$.code").value("METHOD_NOT_ALLOWED"))
                .andExpect(jsonPath("$.message").value("Método HTTP não suportado para este endpoint."));
    }

    @Test
    void deveRetornar404_quandoRotaNaoExiste() throws Exception {
        mvc.perform(get("/rota-que-nao-existe"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Recurso não encontrado."))
                .andExpect(jsonPath("$.path").value("/rota-que-nao-existe"));
    }
}
