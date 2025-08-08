package br.api.tests.service;

import br.api.tests.exception.MensagemNotFoundException;
import br.api.tests.model.Mensagem;
import br.api.tests.repository.MensagemRepository;
import br.api.tests.utils.MensagemHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        var mensagem = MensagemHelper.gerarMensagem();
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
        var id = UUID.fromString("96854457-1ece-4e9d-91f6-0990fed7ed0a");
        var mensagem = MensagemHelper.gerarMensagem();
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
        var id = UUID.fromString("bf416988-aa16-4bff-9dc2-597782fe06dd");
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
        var id = UUID.fromString("28669ca3-2b38-4d96-bff7-1a4e161b8ec1");
        var mensagemAntiga = MensagemHelper.gerarMensagem();
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
    void deveGerarExcecao_QuandoAlterarMensagem_IdNaoExiste() {
        // Arrange
        var id = UUID.fromString("f426d1fa-23f2-4b0d-836b-279bce3c6738");
        var mensagem = MensagemHelper.gerarMensagem();
        mensagem.setId(id);
        when(mensagemRepository.findById(id))
                .thenReturn(Optional.empty());
        // Act and Assert
        assertThatThrownBy(() -> mensagemService.alterarMensagem(id, mensagem))
                .isInstanceOf(MensagemNotFoundException.class)
                .hasMessage("Mensagem não encontrada.");
        verify(mensagemRepository, times(1)).findById(any(UUID.class));
        verify(mensagemRepository, never()).save(any(Mensagem.class));
    }

    @Test
    void deveGerarExcecao_QuandoAlterarMensagem_IdMensagemNovaDiferente() {
        // Arrange
        var id = UUID.fromString("4c68505b-6dff-4db5-ae7e-f63ec9ad0753");
        var mensagemAntiga = MensagemHelper.gerarMensagem();
        mensagemAntiga.setId(id);
        var mensagemNova = MensagemHelper.gerarMensagem();
        mensagemNova.setId(UUID.fromString("ca1fe924-f46e-4c15-a708-af031cd793c3"));
        mensagemNova.setConteudo("Conteudo mensagem");
        when(mensagemRepository.findById(id)).thenReturn(Optional.of(mensagemAntiga));
        // Act and Assert
        assertThatThrownBy(() -> mensagemService.alterarMensagem(id, mensagemNova))
                .isInstanceOf(MensagemNotFoundException.class)
                .hasMessage("Mensagem atualizada não apresenta o ID correto.");
        verify(mensagemRepository, times(1)).findById(any(UUID.class));
        verify(mensagemRepository, never()).save(any(Mensagem.class));
    }

    @Test
    void devePermitirRemoverMensagem() {
        // Arrange
        var id = UUID.fromString("92b0e8f8-9224-4cab-8cea-81e69b7d12ec");
        var mensagem = MensagemHelper.gerarMensagem();
        mensagem.setId(id);
        when(mensagemRepository.findById(id))
                .thenReturn(Optional.of(mensagem));
        doNothing().when(mensagemRepository).deleteById(id);
        // Act
        var mensagemRemovida = mensagemService.removerMensagem(id);
        // Assert
        assertThat(mensagemRemovida).isTrue();
        verify(mensagemRepository, times(1)).findById(any(UUID.class));
        verify(mensagemRepository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void deveGerarExcecao_QuandoRemoverMensagem_IdNaoExiste() {
        // Arrange
        var id = UUID.fromString("3625eb65-1e46-4f44-93f8-247c6d14b726");
        when(mensagemRepository.findById(id))
                .thenReturn(Optional.empty());
        // Act and Assert
        assertThatThrownBy(() -> mensagemService.removerMensagem(id))
                .isInstanceOf(MensagemNotFoundException.class)
                .hasMessage("Mensagem não encontrada.");
        verify(mensagemRepository, times(1)).findById(any(UUID.class));
        verify(mensagemRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    void devePermitirListarMensagem() {
        // Arrange
        Page<Mensagem> listaMensagem = new PageImpl<>(Arrays.asList(
                MensagemHelper.gerarMensagem(),
                MensagemHelper.gerarMensagem()
        ));
        when(mensagemRepository.listarMensagens(any(Pageable.class)))
                .thenReturn(listaMensagem);

        // Act
        var resultadoObtido = mensagemService.listarMensagem(Pageable.unpaged());

        // Assert
        assertThat(resultadoObtido).hasSize(2);
        assertThat(resultadoObtido.getContent())
                .allSatisfy(mensagem -> {
                    assertThat(mensagem)
                            .isNotNull()
                            .isInstanceOf(Mensagem.class);
                });
        verify(mensagemRepository, times(1)).listarMensagens(any(Pageable.class));
    }

}
