package br.com.lumens.unirewards.repository;

import br.com.lumens.unirewards.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    // save() e findById() inclusos por herança
    
    // Método para o login: busca o aluno pelo e-mail herdado de UsuarioAcademico
    Optional<Aluno> findByEmail(String email);
    
    // Verifica se já existe um CPF cadastrado
    boolean existsByCpf(String cpf);
}