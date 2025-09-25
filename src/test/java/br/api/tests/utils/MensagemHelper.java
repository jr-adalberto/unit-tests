package br.api.tests.utils;


import br.api.tests.model.Mensagem;
import br.api.tests.repository.MensagemRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class MensagemHelper {

    public static Mensagem gerarMensagem() {
        return Mensagem.builder()
                .usuario("joe")
                .conteudo("xpto test")
                .build();
    }

    public static Mensagem gerarMensagemCompleta() {
        var timestamp = LocalDateTime.now();
        return Mensagem.builder()
                .id(UUID.randomUUID())
                .usuario("joe")
                .conteudo("xpto test")
                .dataCriacao(timestamp)
                .dataAlteracao(timestamp)
                .build();
    }

    public static Mensagem registrarMensagem(MensagemRepository repository) {
        var mensagem = gerarMensagem();
        mensagem.setId(UUID.randomUUID());
        return repository.save(mensagem);
    }
}