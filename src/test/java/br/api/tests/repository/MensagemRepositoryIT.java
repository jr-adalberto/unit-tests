package br.api.tests.repository;

import br.api.tests.model.Mensagem;
import br.api.tests.utils.MensagemHelper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Sql(scripts = {"classpath:clean.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class MensagemRepositoryIT {

    @Autowired
    private MensagemRepository mensagemRepository;

    @Nested
    class CriarTabela {
        @Test
        void devePermitirCriarTabela() {
            // Arrange & Act
            var totalDeRegistros = mensagemRepository.count();
            // Assert
            assertThat(totalDeRegistros).isZero();
        }
    }

    @Nested
    class RegistrarMensagem {
        @Test
        void devePermitirRegistrarMensagem() {
            // Arrange
            var mensagem = MensagemHelper.gerarMensagem();

            // Act
            var mensagemSalva = mensagemRepository.save(mensagem);

            // Assert
            assertThat(mensagemSalva).isNotNull().isInstanceOf(Mensagem.class);
            assertThat(mensagemSalva.getId()).isNotNull(); // Apenas verificamos se o ID foi gerado
            assertThat(mensagemSalva.getConteudo()).isEqualTo(mensagem.getConteudo());
            assertThat(mensagemSalva.getUsuario()).isEqualTo(mensagem.getUsuario());
        }
    }

    @Nested
    class BuscarMensagem {
        @Test
        void devePermitirBuscarMensagem() {
            // Arrange
            var mensagem = MensagemHelper.gerarMensagem();
            var mensagemSalva = mensagemRepository.save(mensagem);

            // Act
            var mensagemOptional = mensagemRepository.findById(mensagemSalva.getId());

            // Assert
            assertThat(mensagemOptional).isPresent();
            mensagemOptional.ifPresent(mensagemEncontrada -> {
                assertThat(mensagemEncontrada.getId()).isEqualTo(mensagemSalva.getId());
                assertThat(mensagemEncontrada.getConteudo()).isEqualTo(mensagemSalva.getConteudo());
            });
        }

        @Test
        void deveRetornarOptionalVazioQuandoBuscarMensagemComIdInexistente() {
            // Arrange
            var idInexistente = java.util.UUID.randomUUID();

            // Act
            var mensagemOptional = mensagemRepository.findById(idInexistente);

            // Assert
            assertThat(mensagemOptional).isEmpty();
        }
    }

    @Nested
    class RemoverMensagem {
        @Test
        void devePermitirRemoverMensagem() {
            // Arrange
            var mensagem = MensagemHelper.gerarMensagem();
            var mensagemSalva = mensagemRepository.save(mensagem);

            // Act
            mensagemRepository.deleteById(mensagemSalva.getId());
            var mensagemOptional = mensagemRepository.findById(mensagemSalva.getId());

            // Assert
            assertThat(mensagemOptional).isEmpty();
        }
    }

    @Nested
    class ListarMensagem {
        @Test
        void devePermitirListarMensagens() {
            // Arrange
            mensagemRepository.save(MensagemHelper.gerarMensagem());
            mensagemRepository.save(MensagemHelper.gerarMensagem());

            // Act
            var resultadosObtidos = mensagemRepository.findAll();

            // Assert
            assertThat(resultadosObtidos)
                    .hasSize(2)
                    .allSatisfy(mensagem -> assertThat(mensagem.getId()).isNotNull());
        }

        @Test
        void deveRetornarListaVaziaQuandoNaoHouverMensagens() {
            // Arrange: Nenhuma mensagem é salva, pois o clean.sql já limpou o banco

            // Act
            var resultadosObtidos = mensagemRepository.findAll();

            // Assert
            assertThat(resultadosObtidos).isEmpty();
        }
    }
}
