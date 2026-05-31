package br.com.lumens.unirewards.repository;

import br.com.lumens.unirewards.model.Inventario;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    // Método personalizado para buscar o inventário de um aluno específico
    List<Inventario> findByAlunoId(Long alunoId);
}