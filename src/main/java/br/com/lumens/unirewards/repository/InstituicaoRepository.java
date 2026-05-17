package br.com.lumens.unirewards.repository;

import br.com.lumens.unirewards.model.Instituicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface InstituicaoRepository extends JpaRepository<Instituicao, Long> {
    
    Optional<Instituicao> findBySigla(String sigla);
    boolean existsBySigla(String sigla);

}