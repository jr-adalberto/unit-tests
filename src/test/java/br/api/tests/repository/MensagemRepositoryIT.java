package br.api.tests.repository;

import br.api.tests.model.Mensagem;
import br.api.tests.utils.MensagemHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
class MensagemRepositoryIT {

    @Autowired
    private MensagemRepository mensagemRepository;

    @Nested
    class CriarTabela {
        @Test
        void devePermitirCriarTabela() {
            var totalDeRegistros = mensagemRepository.count();
            assertThat(totalDeRegistros).isNotNegative();
        }
    }

    @Nested
    class RegistrarMensagem {
        @Test
        void devePermitirRegistrarMensagem() {
            // Arrange
            var mensagem = MensagemHelper.gerarMensagem();
            mensagem.setId(UUID.randomUUID());

            // Act
            var mensagemRecebida = mensagemRepository.save(mensagem);

            // Assert
            assertThat(mensagemRecebida).isNotNull().isInstanceOf(Mensagem.class);
            assertThat(mensagemRecebida.getId()).isEqualTo(mensagem.getId());
            assertThat(mensagemRecebida.getConteudo()).isEqualTo(mensagem.getConteudo());
            assertThat(mensagemRecebida.getUsuario()).isEqualTo(mensagem.getUsuario());
        }
    }

    @Nested
    class BuscarMensagem {
        @Test
        void devePermitirBuscarMensagem() {
            // Arrange
            var mensagem = MensagemHelper.gerarMensagem();
            mensagem.setId(UUID.randomUUID());
            mensagemRepository.save(mensagem);

            // Act
            var mensagemOptional = mensagemRepository.findById(mensagem.getId());

            // Assert
            assertThat(mensagemOptional).isPresent();
            mensagemOptional.ifPresent(mensagemRecebida ->
                    assertThat(mensagemRecebida.getId()).isEqualTo(mensagem.getId())
            );
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
            mensagemRepository.deleteById(mensagem.getId());
            var mensagemOptional = mensagemRepository.findById(mensagem.getId());

            // Assert
            assertThat(mensagemOptional).isEmpty();
        }
    }

    @Nested
    class ListarMensagem {
        @Test
        void devePermitirListarMensagem() {
            // Arrange
            var mensagem = MensagemHelper.gerarMensagem();
            mensagem.setId(UUID.randomUUID());
            mensagemRepository.save(mensagem);

            // Act
            var resultadosObtidos = mensagemRepository.findAll();

            // Assert
            assertThat(resultadosObtidos).isNotEmpty();
        }
    }
}
