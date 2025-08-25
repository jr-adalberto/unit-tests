package br.api.tests.controller;

import br.api.tests.utils.MensagemHelper;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.assertj.core.api.Fail.fail;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
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
            String xmlPayload = "<mensagem><conteudo>Teste</conteudo><usuario>Ana</usuario></mensagem>";
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(xmlPayload)
                    .when()
                    .post("/mensagens")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("status", equalTo(400))
                    .body("error", equalTo("Bad Request"))
                    .body("path", equalTo("/mensagens"))
                    .body("$", hasKey("timestamp"));
        }
    }

    @Nested
    class BuscarMensagem {
        @Test
        @Sql(scripts = "classpath:data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        void devePermitirBuscarMensagem() throws Exception {
            var id = "bd0e31fd-58b7-44e0-bbff-cc0aaf817b9d";
            when()
                    .get("/mensagens/{id}", id)
                    .then()
                    .statusCode(HttpStatus.OK.value());

        }

        @Test
        void deveGerarExecao_QuandoBuscarMensagem_IdNaoExiste() throws Exception {
            var id = "bd0e31fd-58b7-44e0-bbff-cc0aaf817b9";
            when()
                    .get("/mensagens/{id}", id)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
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
