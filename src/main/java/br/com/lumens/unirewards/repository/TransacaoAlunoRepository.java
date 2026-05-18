package br.com.lumens.unirewards.repository;

import br.com.lumens.unirewards.model.TransacaoAluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransacaoAlunoRepository extends JpaRepository<TransacaoAluno, Long> {
    List<TransacaoAluno> findByRemetenteIdOrderByDataEnvioDesc(Long remetenteId);
    List<TransacaoAluno> findByDestinatarioIdOrderByDataEnvioDesc(Long destinatarioId);
}