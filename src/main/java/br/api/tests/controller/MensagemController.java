package br.api.tests.controller;

import br.api.tests.exception.MensagemNotFoundException;
import br.api.tests.model.Mensagem;
import br.api.tests.service.MensagemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@SuppressWarnings("checkstyle:Indentation")
@RestController
@RequestMapping("mensagens")
@RequiredArgsConstructor
public class MensagemController {


    private final MensagemService mensagemService;

    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mensagem> registrarMensagem(@RequestBody Mensagem mensagem) {
        var mensagemSalva = mensagemService.registrarMensagem(mensagem);
        return new ResponseEntity<>(mensagemSalva, HttpStatus.CREATED);
    }

    @SuppressWarnings({"checkstyle:Indentation", "checkstyle:MissingJavadocMethod"})
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> buscarMensagem(@PathVariable String id) {
        var uuid = UUID.fromString(id);
        try {
            var mensagem = mensagemService.buscarMensagem(uuid);
            return new ResponseEntity<>(mensagem, HttpStatus.OK);
        } catch (MensagemNotFoundException mensagemNotFoundException) {
            return new ResponseEntity<>("ID inválido ou mensagem não encontrada.", HttpStatus.BAD_REQUEST);
        }
    }

    @SuppressWarnings({"checkstyle:Indentation", "checkstyle:MissingJavadocMethod"})
    @PutMapping(value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> alterarMensagem(@PathVariable String id, @RequestBody Mensagem mensagemAtualizada) {
        var uuid = UUID.fromString(id);
        try {
            var mensagemAlterada = mensagemService.alterarMensagem(uuid, mensagemAtualizada);
            return new ResponseEntity<>(mensagemAlterada, HttpStatus.OK);
        } catch (MensagemNotFoundException mensagemNotFoundException) {
            return new ResponseEntity<>("Mensagem atualizada não apresenta o ID correto.", HttpStatus.BAD_REQUEST);
        }
    }
}
