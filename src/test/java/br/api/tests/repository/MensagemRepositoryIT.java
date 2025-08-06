package br.api.tests.repository;

import br.api.tests.model.Mensagem;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class MensagemRepositoryIT {

    @Autowired
    private MensagemRepository mensagemRepository;

    @Test
    void devePermitirCriarTabela() {
        var totalDeRegistros = mensagemRepository.count();
        assertThat(totalDeRegistros).isGreaterThan(0);
    }

    @Test
    void devePermitirRegistrarMensagem() {
        // Arrange
        var id = UUID.randomUUID();
        var mensagem = gerarMensagem();
        mensagem.setId(id);
        // Act
        var mensagemRecebida = mensagemRepository.save(mensagem);
        // Assert
        assertThat(mensagemRecebida)
                .isInstanceOf(Mensagem.class)
                .isNotNull();
        assertThat(mensagemRecebida.getId()).isEqualTo(id);
        assertThat(mensagemRecebida.getConteudo()).isEqualTo(mensagem.getConteudo());
        assertThat(mensagemRecebida.getUsuario()).isEqualTo(mensagem.getUsuario());

    }

    @Test
    void devePermitirBuscarMensagem() {
        // Arrange
        var id = UUID.fromString("88ace1ea-7cde-4276-a44b-ae3b0adead1d");
        // Act
        var mensagemOptional = mensagemRepository.findById(id);
        // Assert
        assertThat(mensagemOptional).isPresent();

        mensagemOptional.ifPresent(mensagemRecebida -> {
            assertThat(mensagemRecebida.getId()).isEqualTo(id);
        });
    }

    @Test
    void devePermitirRemoverMensagem() {
        // Arrange
        var id = UUID.fromString("85e7f770-d4be-4622-9437-bc4c5c1c63b2");
        // Act
        mensagemRepository.deleteById(id);
        var mensagemOptional = mensagemRepository.findById(id);
        // Assert
        assertThat(mensagemOptional).isEmpty();
    }

    @Test
    void devePermitirListarMensagem() {
        // Act
        var resultadosObtidos = mensagemRepository.findAll();
        // Assert
        assertThat(resultadosObtidos).hasSizeGreaterThan(0);

    }

    private Mensagem gerarMensagem() {
        return Mensagem.builder()
                .usuario("Mario")
                .conteudo("conteúdo da mensagem")
                .build();
    }

    private Mensagem registrarMensagem(Mensagem mensagem) {
        return mensagemRepository.save(mensagem);
    }
}
