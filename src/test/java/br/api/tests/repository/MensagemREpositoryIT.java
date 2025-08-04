package br.api.tests.repository;

import br.api.tests.model.Mensagem;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.fail;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class MensagemREpositoryIT {

    @Autowired
    private MensagemRepository mensagemRepository;

    @Test
    void devePermitirCriarTabela() {
        var totalDeRegistros = mensagemRepository.count();
        assertThat(totalDeRegistros).isNotNegative();
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
    void devePermitirRemoverMensagem() {
        fail("teste não implementado.");
    }

    @Test
    void devePermitirListarMensagem() {
        fail("teste não implementado.");
    }

    @Test
    void devePermitirBuscarMensagem() {
        fail("teste não implementado.");
    }

    private Mensagem gerarMensagem() {
        return Mensagem.builder()
                .usuario("Mario")
                .conteudo("conteúdo da mensagem")
                .build();
    }
}
