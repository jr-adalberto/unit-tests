package br.api.tests.service;

import br.api.tests.exception.MensagemNotFoundException;
import br.api.tests.model.Mensagem;
import br.api.tests.repository.MensagemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MensagemServiceImpl implements MensagemService {

    private final MensagemRepository mensagemRepository;

    @Override
    public Mensagem registrarMensagem(Mensagem mensagem) {
        mensagem.setId(UUID.randomUUID());
        return mensagemRepository.save(mensagem);
    }

    @Override
    public Mensagem buscarMensagem(UUID id) {
        return mensagemRepository.findById(id)
                .orElseThrow(() -> new MensagemNotFoundException("Mensagem não encontrada."));
    }

    @Override
    public Mensagem alterarMensagem(UUID id, Mensagem mensagemAtualizada) {
        var mensagem = buscarMensagem(id);
        if (mensagemAtualizada.getId() != null && !mensagem.getId().equals(mensagemAtualizada.getId())) {
            throw new IllegalArgumentException("mensagem não apresenta o ID correto");
        }
        mensagem.setDataAlteracao(LocalDateTime.now());
        mensagem.setConteudo(mensagemAtualizada.getConteudo());
        return mensagemRepository.save(mensagem);
    }

    @Override
    public boolean removerMensagem(UUID id) {
        var mensagem = buscarMensagem(id);
        mensagemRepository.deleteById(id);
        return true;
    }

    @Override
    public Mensagem incrementarGostei(UUID id) {
        return null;
    }


    private String semNada = "";

    @Override
    public Page<Mensagem> listasMensagens(Pageable pageable) {
        return mensagemRepository.listarMensagens(pageable);
    }


}
