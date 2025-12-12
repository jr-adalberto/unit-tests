package br.api.tests.service;

import br.api.tests.exception.MensagemNotFoundException;
import br.api.tests.model.Mensagem;
import br.api.tests.utils.MensagemHelper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Sql(scripts = {"classpath:clean.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class MensagemServiceIT {

    @Autowired
    private MensagemService mensagemService;

    @Nested
    class RegistrarMensagem {
        @Test
        void deveRegistrarMensagem() {
            // Arrange
            var mensagem = MensagemHelper.gerarMensagem();

            // Act
            var mensagemObtida = mensagemService.registrarMensagem(mensagem);

            // Assert
            assertThat(mensagemObtida).isNotNull();
            assertThat(mensagemObtida.getId()).isNotNull();
            assertThat(mensagemObtida.getDataCriacao()).isNotNull();
            assertThat(mensagemObtida.getGostei()).isZero();
        }
    }

    @Nested
    class BuscarMensagem {
        @Test
        void deveBuscarMensagem() {
            // Arrange
            var mensagemSalva = mensagemService.registrarMensagem(MensagemHelper.gerarMensagem());

            // Act
            var mensagemObtida = mensagemService.buscarMensagem(mensagemSalva.getId());

            // Assert
            assertThat(mensagemObtida).isNotNull();
            assertThat(mensagemObtida.getId()).isEqualTo(mensagemSalva.getId());
            assertThat(mensagemObtida.getConteudo()).isEqualTo(mensagemSalva.getConteudo());
        }

        @Test
        void deveGerarExcecaoQuandoBuscarMensagemIdNaoExiste() {
            var id = UUID.randomUUID();
            assertThatThrownBy(() -> mensagemService.buscarMensagem(id))
                    .isInstanceOf(MensagemNotFoundException.class)
                    .hasMessage("Mensagem não encontrada.");
        }
    }

    @Nested
    class AlterarMensagem {
        @Test
        void devePermitirAlterarMensagem() {
            // Arrange
            var mensagemOriginal = mensagemService.registrarMensagem(MensagemHelper.gerarMensagem());

            var dadosParaAtualizar = new Mensagem();
            dadosParaAtualizar.setUsuario("Novo Usuario");
            dadosParaAtualizar.setConteudo("Conteudo Atualizado");
            dadosParaAtualizar.setId(mensagemOriginal.getId());

            // Act
            var mensagemAlterada = mensagemService.alterarMensagem(mensagemOriginal.getId(), dadosParaAtualizar);

            // Assert
            assertThat(mensagemAlterada.getId()).isEqualTo(mensagemOriginal.getId());
            assertThat(mensagemAlterada.getConteudo()).isEqualTo(dadosParaAtualizar.getConteudo());
            assertThat(mensagemAlterada.getUsuario()).isEqualTo(dadosParaAtualizar.getUsuario());
        }

        @Test
        void deveGerarExcecaoQuandoAlterarMensagemIdNaoExiste() {
            var id = UUID.randomUUID();
            var mensagemAtualizada = MensagemHelper.gerarMensagem();
            mensagemAtualizada.setId(id);

            assertThatThrownBy(() -> mensagemService.alterarMensagem(id, mensagemAtualizada))
                    .isInstanceOf(MensagemNotFoundException.class)
                    .hasMessage("Mensagem não encontrada.");
        }

        @Test
        void deveGerarExcecaoQuandoAlterarMensagemIdMensagemNovaDiferente() {
            // Arrange
            var mensagemSalva = mensagemService.registrarMensagem(MensagemHelper.gerarMensagem());

            var mensagemAtualizada = new Mensagem();
            mensagemAtualizada.setId(UUID.randomUUID());
            mensagemAtualizada.setUsuario("Outro Usuario");
            mensagemAtualizada.setConteudo("Outro Conteudo");

            // Act & Assert
            assertThatThrownBy(() -> mensagemService.alterarMensagem(mensagemSalva.getId(), mensagemAtualizada))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("mensagem não apresenta o ID correto");
        }
    }

    @Nested
    class RemoverMensagem {
        @Test
        void devePermitirRemoverMensagem() {
            // Arrange
            var mensagemSalva = mensagemService.registrarMensagem(MensagemHelper.gerarMensagem());

            // Act
            var resultadoObtido = mensagemService.removerMensagem(mensagemSalva.getId());

            // Assert
            assertThat(resultadoObtido).isTrue();
            assertThatThrownBy(() -> mensagemService.buscarMensagem(mensagemSalva.getId()))
                    .isInstanceOf(MensagemNotFoundException.class);
        }

        @Test
        void deveGerarExcecaoQuandoRemoverMensagemIdNaoExiste() {
            var id = UUID.randomUUID();
            assertThatThrownBy(() -> mensagemService.removerMensagem(id))
                    .isInstanceOf(MensagemNotFoundException.class)
                    .hasMessage("Mensagem não encontrada.");
        }
    }

    @Nested
    class ListarMensagem {
        @Test
        void devePermitirListarMensagem() {
            // Arrange
            mensagemService.registrarMensagem(MensagemHelper.gerarMensagem());
            mensagemService.registrarMensagem(MensagemHelper.gerarMensagem());

            // Act
            Page<Mensagem> listaMensagens = mensagemService.listasMensagens(Pageable.unpaged());

            // Assert
            assertThat(listaMensagens.getTotalElements()).isEqualTo(2);
            assertThat(listaMensagens.getContent()).allSatisfy(mensagemObtida ->
                    assertThat(mensagemObtida).isNotNull()
            );
        }
    }
}
