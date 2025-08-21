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
            var mensagem = MensagemHelper.gerarMensagem();
            mensagem.setId(UUID.randomUUID());
            mensagemRepository.save(mensagem);

            // Act
            var mensagemObtida = mensagemService.buscarMensagem(mensagem.getId());

            // Assert
            assertThat(mensagemObtida).isNotNull().isInstanceOf(Mensagem.class);
            assertThat(mensagemObtida.getId()).isEqualTo(mensagem.getId());
            assertThat(mensagemObtida.getUsuario()).isEqualTo(mensagem.getUsuario());
            assertThat(mensagemObtida.getConteudo()).isEqualTo(mensagem.getConteudo());
            assertThat(mensagemObtida.getDataCriacao()).isNotNull();
            assertThat(mensagemObtida.getGostei()).isZero();
        }

        @Test
        void deveGerarExcecao_QuandoBuscarMensagem_IdNaoExiste() {
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
            var mensagem = MensagemHelper.gerarMensagem();
            mensagem.setId(UUID.randomUUID());
            mensagemRepository.save(mensagem);

            var mensagemAtualizada = MensagemHelper.gerarMensagem();
            mensagemAtualizada.setId(mensagem.getId());

            // Act
            var mensagemObtida = mensagemService.alterarMensagem(mensagem.getId(), mensagemAtualizada);

            // Assert
            assertThat(mensagemObtida.getId()).isEqualTo(mensagem.getId());
            assertThat(mensagemObtida.getConteudo()).isEqualTo(mensagemAtualizada.getConteudo());
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdNaoExiste() {
            var id = UUID.randomUUID();
            var mensagemAtualizada = MensagemHelper.gerarMensagem();
            mensagemAtualizada.setId(id);

            assertThatThrownBy(() -> mensagemService.alterarMensagem(id, mensagemAtualizada))
                    .isInstanceOf(MensagemNotFoundException.class)
                    .hasMessage("Mensagem não encontrada.");
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdMensagemNovaDiferente() {
            // Arrange
            var mensagem = MensagemHelper.gerarMensagem();
            mensagem.setId(UUID.randomUUID());
            mensagemRepository.save(mensagem);

            var mensagemAtualizada = MensagemHelper.gerarMensagem();
            mensagemAtualizada.setId(UUID.randomUUID());

            // Assert
            assertThatThrownBy(() -> mensagemService.alterarMensagem(mensagem.getId(), mensagemAtualizada))
                    .isInstanceOf(MensagemNotFoundException.class)
                    .hasMessage("Mensagem atualizada não apresenta o ID correto.");
        }
    }

    @Nested
    class RemoverMensagem {
        @Test
        void devePermitirRemoverMensagem() {
            // Arrange
            var mensagem = MensagemHelper.gerarMensagem();
            mensagem.setId(UUID.randomUUID());
            mensagemRepository.save(mensagem);

            // Act
            var resultadoObtido = mensagemService.removerMensagem(mensagem.getId());

            // Assert
            assertThat(resultadoObtido).isTrue();
        }

        @Test
        void deveGerarExcecao_QuandoRemoverMensagem_IdNaoExiste() {
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
            assertThat(listaMensagens).isNotEmpty().allSatisfy(mensagemObtida ->
                    assertThat(mensagemObtida).isNotNull()
            );
        }
    }
}
