package br.api.tests.controller;

import br.api.tests.exception.MensagemNotFoundException;
import br.api.tests.model.Mensagem;
import br.api.tests.service.MensagemService;
import br.api.tests.utils.MensagemHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class MensagemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MensagemService mensagemService;

    private AutoCloseable openMocks;

    @BeforeEach
    void setup() {
        openMocks = MockitoAnnotations.openMocks(this);
        MensagemController mensagemController = new MensagemController(mensagemService);
        mockMvc = MockMvcBuilders.standaloneSetup(mensagemController)
                .addFilter((request, response, chain) -> {
                    response.setCharacterEncoding("UTF-8");
                    chain.doFilter(request, response);
                })
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Nested
    class RegistrarMensagem {
        @Test
        void devePermitirRegistrarMensagem() throws Exception {
            // Arrange
            var mensagem = MensagemHelper.gerarMensagem();
            when(mensagemService.registrarMensagem(any(Mensagem.class)))
                    .thenAnswer(i -> i.getArgument(0));

            // Act & Assert
            mockMvc.perform(post("/mensagens")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(mensagem))
                    )
                    .andExpect(status().isCreated());
            verify(mensagemService, times(1)).registrarMensagem(any(Mensagem.class));
        }

        @Test
        void deveGerarExecao_QuandoRegistrarMensagem_PayloadXML() throws Exception {
            // Arrange
            String payloadXml = "<mensagem><conteudo>Teste</conteudo><usuario>Ana</usuario></mensagem>";
            // Act & Assert
            mockMvc.perform(post("/mensagens")
                            .contentType(MediaType.APPLICATION_XML)
                            .content(payloadXml)
                    )
                    .andExpect(status().isUnsupportedMediaType());
            verify(mensagemService, never()).registrarMensagem(any(Mensagem.class));
        }
    }

    @Nested
    class BuscarMensagem {
        @Test
        void devePermitirBuscarMensagem() throws Exception {
            // Arrange
            var id = UUID.fromString("81adae3c-c33d-482e-a379-0f707e13a574");
            var mensagem = MensagemHelper.gerarMensagem();
            when(mensagemService.buscarMensagem(any(UUID.class)))
                    .thenReturn(mensagem);
            // Act & Assert
            mockMvc.perform(get("/mensagens/{id}", id))
                    .andExpect(status().isOk());
            verify(mensagemService, times(1)).buscarMensagem(any(UUID.class));
        }

        @Test
        void deveGerarExecao_QuandoBuscarMensagem_IdNaoExiste() throws Exception {
            // Arrange
            var id = UUID.fromString("db3c3577-1021-4c06-8990-4d24502871f3");
            when(mensagemService.buscarMensagem(any(UUID.class)))
                    .thenThrow(MensagemNotFoundException.class);
            // Act & Assert
            mockMvc.perform(get("/mensagens/{id}", id))
                    .andExpect(status().isBadRequest());
            verify(mensagemService, times(1))
                    .buscarMensagem(any(UUID.class));
        }
    }

    @Nested
    class AlterarMensagem {
        @Test
        void devePermitirAlterarMensagem() throws Exception {
            // Arrange
            var id = UUID.fromString("edd228b0-0ebd-457c-ace0-d57850de5766");
            var mensagem = MensagemHelper.gerarMensagem();
            mensagem.setId(id);
            when(mensagemService.alterarMensagem(any(UUID.class), any(Mensagem.class)))
                    .thenAnswer(i -> i.getArgument(1));
            // Act & Assert
            mockMvc.perform(put("/mensagens/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(mensagem)))
                    // para testes     .andDo(print())
                    .andExpect(status().isOk());
            verify(mensagemService, times(1))
                    .alterarMensagem(any(UUID.class), any(Mensagem.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdDaMensagemNovaApresentaValorDiferente() throws Exception {
            // Arrange
            var id = UUID.fromString("7fc506a7-15a0-4b72-a752-2005e59c6dc2");
            var mensagem = MensagemHelper.gerarMensagem();
            mensagem.setId(UUID.fromString("27bc03e7-1fd9-455a-87ff-ca48e7bf9e0b")); // ID diferente do path
            var conteudoExcecao = "mensagem não apresenta o ID correto";

            when(mensagemService.alterarMensagem(any(UUID.class), any(Mensagem.class)))
                    .thenThrow(new IllegalArgumentException(conteudoExcecao));

            // Act & Assert
            mockMvc.perform(put("/mensagens/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(mensagem)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(conteudoExcecao));

            verify(mensagemService, times(1))
                    .alterarMensagem(any(UUID.class), any(Mensagem.class));
        }

        @Test
        void deveGerarExecao_QuandoAlterarMensagem_IdNaoExiste() throws Exception {
            // Arrange
            var id = UUID.fromString("a80a08d4-39f5-4f2a-997c-c5252133a4fe");
            var mensagem = MensagemHelper.gerarMensagem();
            mensagem.setId(id);
            var conteudoExcecao = "Mensagem não encontrada.";

            when(mensagemService.alterarMensagem(any(UUID.class), any(Mensagem.class)))
                    .thenThrow(new MensagemNotFoundException(conteudoExcecao));

            // Act & Assert
            mockMvc.perform(put("/mensagens/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(mensagem)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(conteudoExcecao));

            verify(mensagemService, times(1))
                    .alterarMensagem(any(UUID.class), any(Mensagem.class));
        }


        @Test
        void deveGerarExecao_QuandoPayloadMensagem_PayloadXML() throws Exception {
            // Arrange
            var id = UUID.fromString("edd228b0-0ebd-457c-ace0-d57850de5766");
            String payloadXml = "<mensagem><id>" + id.toString() + "</id><conteudo>Teste</conteudo><usuario>Ana</usuario></mensagem>";
            // Act & Assert
            mockMvc.perform(put("/mensagens/{id}", UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_XML)
                            .content(payloadXml)
                    )
                    .andExpect(status().isUnsupportedMediaType());
            verify(mensagemService, never()).alterarMensagem(any(UUID.class), any(Mensagem.class));
        }
    }

    @Nested
    class RemoverMensagem {
        @Test
        void devePermitirRemoverMensagem() throws Exception {
            // Arrange
            var id = UUID.fromString("a2674580-22e3-49e0-9dc1-0bc5a8f193e0");
            when(mensagemService.removerMensagem(any(UUID.class)))
                    .thenReturn(true);
            // Act & Assert
            mockMvc.perform(delete("/mensagens/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Mensagem removida com sucesso."));
            verify(mensagemService, times(1)).removerMensagem(any(UUID.class));
        }

        @Test
        void deveGerarExecao_QuandoRemoverMensagem_IdNaoExiste() throws Exception {
            // Arrange
            var id = UUID.fromString("0aeea15c-aae9-492d-9298-aaf3222f9b55");
            var mensagemDaExcecao = "Mensagem não encontrada.";
            when(mensagemService.removerMensagem(id))
                    .thenThrow(new MensagemNotFoundException(mensagemDaExcecao));
            // Act & Assert
            mockMvc.perform(delete("/mensagens/{id}", id))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(mensagemDaExcecao));
            verify(mensagemService, times(1)).removerMensagem(any(UUID.class));
        }
    }

    @Nested
    class ListarMensagens {
        @Test
        void devePermitirListarMensagens() throws Exception {
            var mensagem = MensagemHelper.gerarMensagemCompleta();
            Pageable pageable = PageRequest.of(0, 10);
            Page<Mensagem> page = new PageImpl<>(Collections.singletonList(mensagem), pageable, 1L);

            when(mensagemService.listasMensagens(any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/mensagens")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(mensagem.getId().toString()))
                    .andExpect(jsonPath("$.content[0].conteudo").value(mensagem.getConteudo()))
                    .andExpect(jsonPath("$.content[0].usuario").value(mensagem.getUsuario()))
                    .andExpect(jsonPath("$.content[0].dataCriacao").exists())
                    .andExpect(jsonPath("$.content[0].gostei").exists());

            verify(mensagemService, times(1))
                    .listasMensagens(any(Pageable.class));
        }
    }

    static String asJsonString(final Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.writeValueAsString(obj);
    }
}

