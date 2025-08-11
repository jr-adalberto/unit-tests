package br.api.tests.repository;

import br.api.tests.model.Mensagem;
import br.api.tests.utils.MensagemHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class MensagemRepositoryTest {


    @Mock
    private MensagemRepository mensagemRepository;

    AutoCloseable openMocks;

    @BeforeEach
    void setup() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Nested
    class RegistrarMensagem {

        @Test
        void devePermitirResgitrarMensagem() {
            // Arrange
            var mensagem = MensagemHelper.gerarMensagem();
            when(mensagemRepository.save(any(Mensagem.class))).thenReturn(mensagem);
            // Act
            var mensagemArmazenada = mensagemRepository.save(mensagem);
            // Assert
            assertThat(mensagemArmazenada)
                    .isNotNull()
                    .isEqualTo(mensagem);
            verify(mensagemRepository, times(1)).save(any(Mensagem.class));
        }
    }

    @Nested
    class BuscarMensagem {
        @Test
        void devePermitirBuscarMensagem() {
            // Arrange
            var id = UUID.randomUUID();
            var mensagem = MensagemHelper.gerarMensagem();
            mensagem.setId(id);
            when(mensagemRepository.findById(any(UUID.class)))
                    .thenReturn(Optional.of(mensagem));
            // Act
            var mensagemOptional = mensagemRepository.findById(id);
            // Assert
            assertThat(mensagemOptional)
                    .isPresent()
                    .containsSame(mensagem);
            mensagemOptional.ifPresent(mensagemRecebida -> {
                assertThat(mensagemRecebida.getId()).isEqualTo(mensagem.getId());
                assertThat(mensagemRecebida.getConteudo()).isEqualTo(mensagem.getConteudo());
            });
            verify(mensagemRepository, times(1)).findById(any(UUID.class));
        }
    }

    @Nested
    class RemoverMensagem {
        @Test
        void devePermitirRemoverMensagem() {
            // Arrange
            var id = UUID.randomUUID();
            doNothing().when(mensagemRepository).deleteById(any(UUID.class));
            // Act
            mensagemRepository.deleteById(id);
            // Assert
            verify(mensagemRepository, times(1)).deleteById(any(UUID.class));
        }
    }

    @Nested
    class ListarMensagens {
        @Test
        void devePermitirListarMensagens() {
            // Arrange
            var mensagem1 = MensagemHelper.gerarMensagem();
            var mensagem2 = MensagemHelper.gerarMensagem();
            var mensagemList = Arrays.asList(
                    mensagem1,
                    mensagem2);
            when(mensagemRepository.findAll()).thenReturn(mensagemList);
            // Act
            var mensagensRecebidas = mensagemRepository.findAll();
            // Assert
            assertThat(mensagensRecebidas)
                    .hasSize(2)
                    .containsExactlyInAnyOrder(mensagem1, mensagem2);
            verify(mensagemRepository, times(1)).findAll();
        }
    }
}
