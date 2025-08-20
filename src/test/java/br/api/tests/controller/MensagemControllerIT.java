package br.api.tests.controller;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Fail.fail;

public class MensagemControllerIT {


    @Nested
    class RegistrarMensagem {
        @Test
        void devePermitirRegistrarMensagem() throws Exception {
        fail("Not yet implemented");
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
