package br.api.tests.bdd;

import br.api.tests.model.Mensagem;
import br.api.tests.utils.MensagemHelper;
import io.cucumber.java.PendingException;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import io.restassured.response.Response;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

public class StepDefinition {

    @LocalServerPort
    private int port;

    private Response response;
    private Mensagem mensagemResponse;

    private String getEndpoint() {
        return "http://localhost:" + port + "/mensagens";
    }

    @Quando("submeter uma nova mensagem")
    public Mensagem submeterNovaMensagem() {
        var mensagemRequest = MensagemHelper.gerarMensagem();
        response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(mensagemRequest)
                .when()
                .post(getEndpoint());
        return response.then().extract().as(Mensagem.class);
    }

    @Então("a mensagem é registrada com sucesso")
    public void mensagemRegistradaComSucesso() {
        response.then()
                .statusCode(HttpStatus.CREATED.value())
                .body(matchesJsonSchemaInClasspath("schemas/mensagem.schema.json"));
    }

    @Dado("que uma mensagem já foi publicada")
    public void mensagemJaPublicada() {
        mensagemResponse = submeterNovaMensagem();
    }

    @Quando("requisitar a busca da mensagem")
    public void requisitarBuscarMensagem() {
        response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(getEndpoint() + "/{id}", mensagemResponse.getId().toString());
    }

    @Então("a mensagem é exibida com sucesso")
    public void mensagemExibidaComSucesso() {
        response.then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("schemas/mensagem.schema.json"));
    }

    @Quando("requisitar a lista da mensagem")
    public void requisitarListaMensagens() {
        response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(getEndpoint());
    }

    @Então("as mensagens são exibidas com sucesso")
    public void mensagensSaoExibidasComSucesso() {
        response.then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("schemas/mensagem.page.schema.json"))
                .body("number", equalTo(0))
                .body("size", equalTo(10));
    }

    @Quando("requisitar a alteração da mensagem")
    public void requisitarAlteracaoDaMensagem() {
        mensagemResponse.setConteudo("novo conteudo");
        response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(mensagemResponse)
                .when()
                .put(getEndpoint() + "/{id}", mensagemResponse.getId().toString());
    }

    @Então("a mensagem é atualizada com sucesso")
    public void mensagemAtualizadaComSucesso() {
        response.then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("schemas/mensagem.schema.json"));
    }

    @Quando("requisitar a exclusão da mensagem")
    public void requisitarExclusaoDaMensagem() {
        response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(getEndpoint() + "/{id}", mensagemResponse.getId().toString());
    }

    @Então("a mensagem é removida com sucesso")
    public void mensagemRemovidaComSucesso() {
        response.then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("Mensagem removida com sucesso."));
    }

    @Dado("passo em desenvolvimento")
    public void passo_em_desenvolvimento() {
        throw new PendingException("TODO: Cenario em desenvolvimento");
    }
}
