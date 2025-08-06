package br.api.tests.service;

import br.api.tests.exception.MensagemNotFoundException;
import br.api.tests.model.Mensagem;
import br.api.tests.repository.MensagemRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MensagemServiceTest {

    private MensagemService mensagemService;

    @Mock
    private MensagemRepository mensagemRepository;

    AutoCloseable mock;

    @BeforeEach
    void setup() {
        mock = MockitoAnnotations.openMocks(this);
        mensagemService = new MensagemServiceImpl(mensagemRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    @Test
    void devePermitirRegistrarMensagem() {
        // Arrange
        var mensagem = gerarMensagem();
        when(mensagemRepository.save(any(Mensagem.class)))
                .thenAnswer(i -> i.getArgument(0));
        // Act
        var mensagemRegistrada = mensagemService
                .registrarMensagem(mensagem);
        //Assert
        assertThat(mensagemRegistrada).isInstanceOf(Mensagem.class)
                .isNotNull();
        assertThat(mensagemRegistrada.getConteudo()).isEqualTo(mensagem.getConteudo());
        assertThat(mensagemRegistrada.getUsuario()).isEqualTo(mensagem.getUsuario());
        assertThat(mensagem.getId()).isNotNull();
        verify(mensagemRepository, times(1)).save(any(Mensagem.class));
    }

    @Test
    void devePermitirBuscarMensagem() {
        // Arrange
        var id = UUID.randomUUID();
        var mensagem = gerarMensagem();
        mensagem.setId(id);
        when(mensagemRepository.findById(id))
                .thenReturn(Optional.of(mensagem));
        // Act
        var mensagemObtida = mensagemService.buscarMensagem(id);
        // Assert
        assertThat(mensagemObtida).isEqualTo(mensagem);
        verify(mensagemRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void deveGerarExcecao_QuandoBuscarMensagem_IdNaoExiste() {
        // Arrange
        var id = UUID.randomUUID();
        when(mensagemRepository.findById(id))
                .thenReturn(Optional.empty());
        // Act and Assert
        assertThatThrownBy(() -> mensagemService.buscarMensagem(id))
                .isInstanceOf(MensagemNotFoundException.class)
                .hasMessage("Mensagem não encontrada.");
        verify(mensagemRepository, times(1)).findById(id);
    }

    @Test
    void devePermitirAlterarMensagem() {
        // Arrange
        var id = UUID.randomUUID();
        var mensagemAntiga = gerarMensagem();
        mensagemAntiga.setId(id);
        var mensagemNova = new Mensagem();
        mensagemNova.setId(mensagemAntiga.getId());
        mensagemNova.setUsuario(mensagemAntiga.getUsuario());
        mensagemNova.setConteudo("Conteudo de mensagem");
        when(mensagemRepository.findById(id))
                .thenReturn(Optional.of(mensagemAntiga));
        when(mensagemRepository.save(mensagemNova))
                .thenAnswer(i -> i.getArgument(0));
        // Act
        var mensagemObtida = mensagemService.alterarMensagem(id, mensagemNova);
        // Assert
        assertThat(mensagemObtida).isInstanceOf(Mensagem.class).isNotNull();
        assertThat(mensagemObtida.getId()).isEqualTo(mensagemNova.getId());
        assertThat(mensagemObtida.getUsuario()).isEqualTo(mensagemNova.getUsuario());
        assertThat(mensagemObtida.getConteudo()).isEqualTo(mensagemNova.getConteudo());
        verify(mensagemRepository, times(1)).findById(id);
        verify(mensagemRepository, times(1)).save(mensagemNova);
    }

    @Test
    void deveGerarExcecao_QuandoALterarMensagem_IdNaoAPresentaMensagem(){
        // Arrange
        var id = UUID.randomUUID();
        var mensagem = gerarMensagem();
        mensagem.setId(id);
        when(mensagemRepository.findById(id))
                .thenReturn(Optional.empty());
        // Act and Assert
        assertThatThrownBy(()-> mensagemService.alterarMensagem(id, mensagem))
                .isInstanceOf(MensagemNotFoundException.class)
                .hasMessage("Mensagem não encontrada.");
        verify(mensagemRepository, times(1)).findById(any(UUID.class));
        verify(mensagemRepository, never()).save(any(Mensagem.class));
    }

    @Test
    void devePermitirRemoverMensagem() {
        fail("teste não implementado");
    }

    @Test
    void devePermitirListarMensagem() {
        fail("teste não implementado");
    }

    private Mensagem gerarMensagem() {
        return Mensagem.builder()
                .usuario("Mario")
                .conteudo("conteúdo da mensagem")
                .build();
    }

}
