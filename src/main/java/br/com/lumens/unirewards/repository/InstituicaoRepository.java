package br.com.lumens.unirewards.repository;

import br.com.lumens.unirewards.model.Instituicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface InstituicaoRepository extends JpaRepository<Instituicao, Long> {
    
    // Método útil para validar se uma instituição já existe pelo nome
    Optional<Instituicao> findByNome(String nome);
    
    // Verifica existência por nome para evitar duplicatas no cadastro de instituições
    boolean existsByNome(String nome);
}