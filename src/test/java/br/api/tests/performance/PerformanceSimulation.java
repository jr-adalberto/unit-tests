package br.api.tests.performance;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;


public class PerformanceSimulation extends Simulation {

    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080")
            .contentTypeHeader("application/json");

    ChainBuilder adicionarMensagemRequest =
            exec(
                    http("adicionar mensagem")
                            .post("/mensagens")
                            .body(StringBody("{ \"usuario\": \"user\", \"conteudo\": \"demo\" }"))
                            .check(status().is(201))
                            .check(jsonPath("$.id").saveAs("mensagemId"))
            );

    ChainBuilder buscarMensagemRequest =
            exec(
                    http("buscar mensagem")
                            .get("/mensagens/#{mensagemId}")
                            .check(status().is(200))
            );

    ChainBuilder removerMensagemRequest =
            exec(
                    http("remover mensagem")
                            .delete("/mensagens/#{mensagemId}")
                            .check(status().is(200))
            );


    ScenarioBuilder cenarioAdicionarMensagem = scenario("Adicionar mensagem")
            .exec(adicionarMensagemRequest);

    ScenarioBuilder cenarioBuscarMensagem = scenario("Buscar mensagem")
            .exec(adicionarMensagemRequest)
            .exec(buscarMensagemRequest);

    ScenarioBuilder cenarioRemoverMensagem = scenario("Remover mensagem")
            .exec(adicionarMensagemRequest)
            .exec(removerMensagemRequest);


    {
        setUp(
                cenarioAdicionarMensagem.injectOpen(
                        rampUsersPerSec(1)
                                .to(2)
                                .during(Duration.ofSeconds(10)),
                        constantUsersPerSec(2)
                                .during(Duration.ofSeconds(20)),
                        rampUsersPerSec(2)
                                .to(1)
                                .during(Duration.ofSeconds(10))
                ),
                cenarioBuscarMensagem.injectOpen(
                        rampUsersPerSec(1)
                                .to(10)
                                .during(Duration.ofSeconds(10)),
                        constantUsersPerSec(10)
                                .during(Duration.ofSeconds(20)),
                        rampUsersPerSec(10)
                                .to(1)
                                .during(Duration.ofSeconds(10))),
                cenarioRemoverMensagem.injectOpen(
                        rampUsersPerSec(1)
                                .to(5)
                                .during(Duration.ofSeconds(10)),
                        constantUsersPerSec(5)
                                .during(Duration.ofSeconds(20)),
                        rampUsersPerSec(5)
                                .to(1)
                                .during(Duration.ofSeconds(10)))
        ).protocols(httpProtocol)
                .assertions(
                        global().responseTime().max().lt(500),
                        global().failedRequests().count().is(0L)
                );
    }
}
