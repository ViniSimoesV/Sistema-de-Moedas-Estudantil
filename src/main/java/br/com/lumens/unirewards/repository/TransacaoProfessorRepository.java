package br.com.lumens.unirewards.repository;

import br.com.lumens.unirewards.model.TransacaoProfessor;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransacaoProfessorRepository extends JpaRepository<TransacaoProfessor, Long> {

}