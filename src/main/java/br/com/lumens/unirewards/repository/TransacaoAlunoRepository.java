package br.com.lumens.unirewards.repository;

import br.com.lumens.unirewards.model.TransacaoAluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransacaoAlunoRepository extends JpaRepository<TransacaoAluno, Long> {
}