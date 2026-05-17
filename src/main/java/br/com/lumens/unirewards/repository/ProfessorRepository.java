package br.com.lumens.unirewards.repository;

import br.com.lumens.unirewards.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    
    // Busca todos os professores que pertencem a uma instituição específica
    List<Professor> findByInstituicaoId(Long instituicaoId);
    
    // Útil para futuras validações e evitar professores duplicados
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
}