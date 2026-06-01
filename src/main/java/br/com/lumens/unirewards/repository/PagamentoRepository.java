package br.com.lumens.unirewards.repository;

import br.com.lumens.unirewards.model.Pagamento;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    List<Pagamento> findByAlunoId(Long alunoId);
}