package br.api.tests.controller;

import br.api.tests.model.Mensagem;
import br.api.tests.utils.MensagemHelper;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Sql(scripts = {"classpath:clean.sql", "classpath:data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
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
        void devePermitirRegistrarMensagem() {
            var mensagem = MensagemHelper.gerarMensagem();
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(mensagem)
                    .log().all()
                    .when()
                    .post("/mensagens")
                    .then()
                    .log().all()
                    .statusCode(HttpStatus.CREATED.value())
                    .body(matchesJsonSchemaInClasspath("schemas/mensagem.schema.json"));
        }

        @Test
        void deveGerarExecao_QuandoRegistrarMensagem_PayloadXML() {
            String xmlPayload = "<mensagem><conteudo>Teste</conteudo><usuario>Ana</usuario></mensagem>";
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(xmlPayload)
                    .log().all()
                    .when()
                    .post("/mensagens")
                    .then()
                    .log().all()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(matchesJsonSchemaInClasspath("schemas/error.schema.json"))
                    .body("error", equalTo("Bad Request"))
                    .body("path", equalTo("/mensagens"));
        }
    }

    @Nested
    class BuscarMensagem {
        @Test
        void devePermitirBuscarMensagem() {
            var id = "bd0e31fd-58b7-44e0-bbff-cc0aaf817b9d";
            when()
                    .get("/mensagens/{id}", id)
                    .then()
                    .statusCode(HttpStatus.OK.value());

        }

        @Test
        void deveGerarExecao_QuandoBuscarMensagem_IdNaoExiste() {
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
        void devePermitirAlterarMensagem() {
            var id = UUID.fromString("4ff2f92d-c45d-4b5b-b31a-dbd552234574");
            var mensagem = Mensagem.builder()
                    .id(id)
                    .usuario("Maria")
                    .conteudo("Mensagem 02")
                    .build();

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(mensagem)
                    .when()
                    .put("/mensagens/{id}", id)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("schemas/mensagem.schema.json"));
        }

        @Test
        void deveGerarExecao_QuandoAlterarMensagem_IdNaoExiste() {
            var id = UUID.fromString("4ff2f92d-c45d-4b5b-b31a-dbd55223457");
            var mensagem = Mensagem.builder()
                    .id(id)
                    .usuario("Maria")
                    .conteudo("Mensagem two")
                    .build();

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(mensagem)
                    .when()
                    .put("/mensagens/{id}", id)
                    .then()
                    .log().all()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body(equalTo("Mensagem não encontrada."));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdDaMensagemNovaApresentaValorDiferente() {
            var id = UUID.fromString("4ff2f92d-c45d-4b5b-b31a-dbd552234574");
            var mensagem = Mensagem.builder()
                    .id(UUID.fromString("4ff2f92d-c45d-4b5b-b31a-dbd55223457"))
                    .usuario("Maria")
                    .conteudo("Mensagem two")
                    .build();

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(mensagem)
                    .when()
                    .put("/mensagens/{id}", id)
                    .then()
                    .log().all()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(equalTo("mensagem não apresenta o ID correto"));
        }

        @Test
        void deveGerarExecao_QuandoPayloadMensagem_PayloadXML() {
            var id = UUID.fromString("4ff2f92d-c45d-4b5b-b31a-dbd552234574");
            String xmlPayload = "<mensagem><id>4ff2f92d-c45d-4b5b-b31a-dbd552234574</id><conteudo>Teste</conteudo><usuario>Ana</usuario></mensagem>";

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(xmlPayload)
                    .when()
                    .put("/mensagens/{id}", id)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(matchesJsonSchemaInClasspath("schemas/error.schema.json"))
                    .body("error", equalTo("Bad Request"))
                    .body("path", equalTo("/mensagens/4ff2f92d-c45d-4b5b-b31a-dbd552234574"))
                    .body("path", containsString("/mensagens"));
        }

        @Nested
        class RemoverMensagem {
            @Test
            void devePermitirRemoverMensagem() {
                var id = UUID.fromString("bd0e31fd-58b7-44e0-bbff-cc0aaf817b9d");
                when()
                        .delete("/mensagens/{id}", id)
                        .then()
                        .statusCode(HttpStatus.OK.value())
                        .body(equalTo("Mensagem removida com sucesso."));
            }

            @Test
            void deveGerarExecao_QuandoRemoverMensagem_IdNaoExiste() {
                var id = "5f789b39-4295-42c1-a65b-cfca5b987db3";
                given()
                        .filter(new AllureRestAssured())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when()
                        .delete("/mensagens/{id}", id)
                        .then()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .body(equalTo("Mensagem não encontrada."));
            }

            @Nested
            class ListarMensagens {
                @Test
                void devePermitirListarMensagens() {
                    given()
                            .queryParam("page", 0)
                            .queryParam("size", 10)
                            .when()
                            .get("/mensagens")
                            .then()
                            .log().all()
                            .statusCode(HttpStatus.OK.value())
                            .body(matchesJsonSchemaInClasspath("schemas/mensagem.page.schema.json"));
                }

                @Test
                void devePermitirListarMensagens_QuandoNaoInformadoPaginacao() {
                    when()
                            .get("/mensagens")
                            .then()
                            .log().all()
                            .statusCode(HttpStatus.OK.value())
                            .body(matchesJsonSchemaInClasspath("schemas/mensagem.page.schema.json"));
                }
            }
        }
    }
}
