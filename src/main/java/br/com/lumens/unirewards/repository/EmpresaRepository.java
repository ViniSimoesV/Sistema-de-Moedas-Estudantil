package br.com.lumens.unirewards.repository;

import br.com.lumens.unirewards.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    // save() e findById() inclusos por herança
    
    // Método para o login da empresa[cite: 7]
    Optional<Empresa> findByCnpj(String cnpj);
    
    // Útil para validação no cadastro
    boolean existsByCnpj(String cnpj);
}