package br.api.tests.controller;

import br.api.tests.exception.MensagemNotFoundException;
import br.api.tests.model.Mensagem;
import br.api.tests.service.MensagemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@SuppressWarnings("checkstyle:Indentation")
@RestController
@RequestMapping("mensagens")
@RequiredArgsConstructor
@Slf4j
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

    @GetMapping(
            value = "",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Mensagem>> listarMensagens(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        log.info("requisição para listar mensagens foi efetuada: Página={}, Tamanho={}", page, size);
        Page<Mensagem> mensagens = mensagemService.listasMensagens(pageable);
        return new ResponseEntity<>(mensagens, HttpStatus.OK);
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
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (MensagemNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> removerMensagem(@PathVariable String id) {
        var uuid = UUID.fromString(id);
        try {
            mensagemService.removerMensagem(uuid);
            return new ResponseEntity<>("Mensagem removida com sucesso.", HttpStatus.OK);
        } catch (MensagemNotFoundException mensagemNotFoundException) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(mensagemNotFoundException.getMessage());
        }
    }
}
