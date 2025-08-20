package br.api.tests.service;

import br.api.tests.exception.MensagemNotFoundException;
import br.api.tests.model.Mensagem;
import br.api.tests.repository.MensagemRepository;
import br.api.tests.utils.MensagemHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class MensagemServiceIT {

    @Autowired
    private MensagemRepository mensagemRepository;

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
            assertThat(mensagemObtida).isNotNull().isInstanceOf(Mensagem.class);
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
            var id = UUID.fromString("88ace1ea-7cde-4276-a44b-ae3b0adead1d");
            var mensagemObtida = mensagemService.buscarMensagem(id);
            // Assert
            assertThat(mensagemObtida).isNotNull().isInstanceOf(Mensagem.class);
            assertThat(mensagemObtida.getId()).isNotNull().isEqualTo(id);
            assertThat(mensagemObtida.getUsuario()).isNotNull().isEqualTo("James");
            assertThat(mensagemObtida.getConteudo()).isNotNull().isEqualTo("Mensagem one");
            assertThat(mensagemObtida.getDataCriacao()).isNotNull();
            assertThat(mensagemObtida.getGostei()).isEqualTo(0);
        }

        @Test
        void deveGerarExcecao_QuandoBuscarMensagem_IdNaoExiste() {
            var id = UUID.fromString("10b75e75-2685-405a-bcdf-bdd4410381ba");
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
            var id = UUID.fromString("85e7f770-d4be-4622-9437-bc4c5c1c63b2");
            var mensagemAtualizada = MensagemHelper.gerarMensagem();
            mensagemAtualizada.setId(id);
            var mensagemObtida = mensagemService.alterarMensagem(id, mensagemAtualizada);
            // Arrange
            assertThat(mensagemObtida.getId()).isEqualTo(id);
            assertThat(mensagemObtida.getConteudo()).isEqualTo(mensagemAtualizada.getConteudo());

            assertThat(mensagemObtida.getUsuario()).isNotEqualTo(mensagemAtualizada.getUsuario());
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdNaoExiste() {
            // Arrange
            var id = UUID.fromString("44a8d226-2fcf-48d0-9985-c58d263579c4");
            var mensagemAtualizada = MensagemHelper.gerarMensagem();
            mensagemAtualizada.setId(id);
            // Assert
            assertThatThrownBy(() -> mensagemService.alterarMensagem(id, mensagemAtualizada))
                    .isInstanceOf(MensagemNotFoundException.class)
                    .hasMessage("Mensagem não encontrada.");
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdMensagemNovaDiferente() {
            // Arrange
            var id = UUID.fromString("85e7f770-d4be-4622-9437-bc4c5c1c63b2");
            var mensagemAtualizada = MensagemHelper.gerarMensagem();
            mensagemAtualizada.setId(UUID.fromString("e75909d9-18d9-4435-bc75-380a400cb14b"));
            // Assert
            assertThatThrownBy(() -> mensagemService.alterarMensagem(id, mensagemAtualizada))
                    .isInstanceOf(MensagemNotFoundException.class)
                    .hasMessage("Mensagem atualizada não apresenta o ID correto.");
        }
    }

    @Nested
    class RemoverMensagem {
        @Test
        void devePermitirRemoverMensagem() {
            // Arrange
            var id = UUID.fromString("a2674580-22e3-49e0-9dc1-0bc5a8f193e0");
            var resultadoObtido = mensagemService.removerMensagem(id);
            // Assert
            assertThat(resultadoObtido).isTrue();
        }

        @Test
        void deveGerarExcecao_QuandoRemoverMensagem_IdNaoExiste() {
            // Arrange
            var id = UUID.fromString("971b1e85-b5a6-44ef-a33c-e140f976fc41");
            // Assert
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
            Page<Mensagem> listaMensagens = mensagemService.listasMensagens(Pageable.unpaged());
            // Assert
            assertThat(listaMensagens).hasSize(3).allSatisfy(mensagemObtida -> {
                assertThat(mensagemObtida).isNotNull();
            });
        }
    }

}
