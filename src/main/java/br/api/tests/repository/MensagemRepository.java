package br.api.tests.repository;

import br.api.tests.model.Mensagem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
public interface MensagemRepository extends JpaRepository<Mensagem, UUID> {
}
