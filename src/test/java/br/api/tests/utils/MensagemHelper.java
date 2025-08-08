package br.api.tests.utils;

import br.api.tests.model.Mensagem;

public abstract class MensagemHelper {

    public static Mensagem gerarMensagem() {
        return Mensagem.builder()
                .usuario("Mario")
                .conteudo("conteúdo da mensagem")
                .build();
    }
}
