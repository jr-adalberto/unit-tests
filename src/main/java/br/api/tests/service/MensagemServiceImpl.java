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

        mensagem.setConteudo(mensagemAtualizada.getConteudo());
        mensagem.setUsuario(mensagemAtualizada.getUsuario());
        mensagem.setDataAlteracao(LocalDateTime.now());

        return mensagemRepository.save(mensagem);
    }

    @Override
    public boolean removerMensagem(UUID id) {
        buscarMensagem(id);
        mensagemRepository.deleteById(id);
        return true;
    }

    @Override
    public Mensagem incrementarGostei(UUID id) {
        return null;
    }

    @Override
    public Page<Mensagem> listasMensagens(Pageable pageable) {
        return mensagemRepository.findAll(pageable);
    }
}
