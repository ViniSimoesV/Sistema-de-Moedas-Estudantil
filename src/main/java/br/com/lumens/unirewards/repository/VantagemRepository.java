package br.com.lumens.unirewards.repository;

import br.com.lumens.unirewards.model.Vantagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VantagemRepository extends JpaRepository<Vantagem, Long> {
    
    // Busca todas as vantagens cadastradas por uma empresa específica
    List<Vantagem> findByEmpresaId(Long empresaId);
    
    // Busca vantagens que custam até um determinado valor
    List<Vantagem> findByCustoLessThanEqual(Integer custo);
    
    // Busca vantagens filtrando pelo ramo de atuação da empresa parceira
    List<Vantagem> findByEmpresa_RamoAtuacaoContainingIgnoreCase(String ramoAtuacao);

    // Busca as 3 vantagens mais resgatadas de uma empresa
    List<Vantagem> findTop3ByEmpresaIdOrderByQuantidadeResgatesDesc(Long empresaId);
}