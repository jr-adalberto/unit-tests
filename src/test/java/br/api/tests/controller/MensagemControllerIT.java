package br.api.tests.controller;

import br.api.tests.utils.MensagemHelper;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Fail.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MensagemControllerIT {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setup() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }


    @Nested
    class RegistrarMensagem {
        @Test
        void devePermitirRegistrarMensagem() throws Exception {
            // Given
            var mensagem = MensagemHelper.gerarMensagem();
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(mensagem)
                    .when()
                    .post("/mensagens")
                    .then().statusCode(HttpStatus.CREATED.value());
        }

        @Test
        void deveGerarExecao_QuandoRegistrarMensagem_PayloadXML() throws Exception {
            fail("Not yet implemented");
        }
    }

    @Nested
    class BuscarMensagem {
        @Test
        void devePermitirBuscarMensagem() throws Exception {
            fail("Not yet implemented");
        }

        @Test
        void deveGerarExecao_QuandoBuscarMensagem_IdNaoExiste() throws Exception {
            fail("Not yet implemented");
        }
    }

    @Nested
    class AlterarMensagem {
        @Test
        void devePermitirAlterarMensagem() throws Exception {
            fail("Not yet implemented");
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdDaMensagemNovaApresentaValorDiferente() throws Exception {
            fail("Not yet implemented");
        }

        @Test
        void deveGerarExecao_QuandoAlterarMensagem_IdNaoExiste() throws Exception {
            fail("Not yet implemented");
        }

        @Test
        void deveGerarExecao_QuandoPayloadMensagem_PayloadXML() throws Exception {
            fail("Not yet implemented");
        }

        @Nested
        class RemoverMensagem {
            @Test
            void devePermitirRemoverMensagem() throws Exception {
                fail("Not yet implemented");
            }

            @Test
            void deveGerarExecao_QuandoRemoverMensagem_IdNaoExiste() throws Exception {
                fail("Not yet implemented");
            }

            @Nested
            class ListarMensagens {
                @Test
                void devePermitirListarMensagens() throws Exception {
                    fail("Not yet implemented");
                }
            }
        }
    }
}
