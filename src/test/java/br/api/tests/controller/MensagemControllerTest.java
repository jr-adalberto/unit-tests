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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                    .andExpect(status().isOk());
            verify(mensagemService, times(1))
                    .alterarMensagem(any(UUID.class), any(Mensagem.class));
        }

        @Test
        void deveGerarExecao_QuandoAlterarMensagem_IdNaoExiste() throws Exception {
            // Arrange
            var id = UUID.fromString("7fc506a7-15a0-4b72-a752-2005e59c6dc2");
            var mensagem = MensagemHelper.gerarMensagem();
            mensagem.setId(id);
            var conteudoExcecao = "Mensagem atualizada não apresenta o ID correto.";
            when(mensagemService.alterarMensagem(any(UUID.class), any(Mensagem.class)))
                    .thenThrow(new MensagemNotFoundException(conteudoExcecao));
            // Act & Assert
            mockMvc.perform(put("/mensagens/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(mensagem)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Mensagem atualizada não apresenta o ID correto."));
            verify(mensagemService, times(1))
                    .alterarMensagem(any(UUID.class), any(Mensagem.class));
        }

        @Test
        void deveGerarExecao_QuandoAlterarMensagem_IdDaMensagemNovaApresentaValorDiferente() {
            fail("Not yet implemented");
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
        void devePermitirRemoverMensagem() {
            fail("Not yet implemented");
        }

        @Test
        void deveGerarExecao_QuandoRemoverMensagem_IdNaoExiste() {
            fail("Not yet implemented");
        }
    }

    @Nested
    class ListarMensagens {
        @Test
        void devePermitirListarMensagens() {
            fail("Not yet implemented");
        }
    }

    private static String asJsonString(final Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.writeValueAsString(obj);
    }

}
