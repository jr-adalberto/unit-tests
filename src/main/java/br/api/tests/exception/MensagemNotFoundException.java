package br.api.tests.exception;

public class MensagemNotFoundException extends RuntimeException {

    public MensagemNotFoundException(String mensagem) {
        super(mensagem);
    }
}
