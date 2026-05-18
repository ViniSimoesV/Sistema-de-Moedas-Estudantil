package br.com.lumens.unirewards.repository;

import br.com.lumens.unirewards.model.TransacaoProfessor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransacaoProfessorRepository extends JpaRepository<TransacaoProfessor, Long> {
    List<TransacaoProfessor> findByProfessorIdOrderByDataEnvioDesc(Long professorId);
    List<TransacaoProfessor> findByAlunoIdOrderByDataEnvioDesc(Long alunoId);
}