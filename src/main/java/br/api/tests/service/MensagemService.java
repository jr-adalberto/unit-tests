package br.api.tests.service;


import br.api.tests.model.Mensagem;

import java.util.UUID;

public interface MensagemService {

    Mensagem registrarMensagem(Mensagem mensagem);
    Mensagem buscarMensagem(UUID id);
    Mensagem alterarMensagem(UUID id, Mensagem mensagemAtualizada);
    boolean removerMensagem(UUID id);

}
