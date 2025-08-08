package br.api.tests.service;

import br.api.tests.exception.MensagemNotFoundException;
import br.api.tests.model.Mensagem;
import br.api.tests.repository.MensagemRepository;
import br.api.tests.utils.MensagemHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class MensagemServiceIT {

    @Autowired
    private MensagemRepository mensagemRepository;

    @Autowired
    private MensagemService mensagemService;

    @Test
    void devePermitirRegistrarMensagem() {
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

    @Test
    void deveBuscarMensagem() {
        // Arrange
        var id = UUID.fromString("88ace1ea-7cde-4276-a44b-ae3b0adead1d");
        // Act
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
                .hasMessage("Mensagem não apresentada");
    }

    @Test
    void devePermitirAlterarMensagem() {
        fail("teste não implementado");
    }

    @Test
    void deveGerarExcecao_QuandoAlterarMensagem_IdNaoExiste() {
        fail("teste não implementado");
    }

    @Test
    void deveGerarExcecao_QuandoAlterarMensagem_IdMensagemNovaDiferente() {
        fail("teste não implementado");
    }

    @Test
    void devePermitirRemoverMensagem() {
        fail("teste não implementado");
    }

    @Test
    void deveGerarExcecao_QuandoRemoverMensagem_IdNaoExiste() {
        fail("teste não implementado");
    }

    @Test
    void devePermitirListarMensagem() {
        fail("teste não implementado");
    }

}
