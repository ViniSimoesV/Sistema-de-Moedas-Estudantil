package br.com.lumens.unirewards.service;

import br.com.lumens.unirewards.model.Aluno;
import br.com.lumens.unirewards.repository.AlunoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AlunoService {

    @Autowired
    private AlunoRepository alunoRepository;

    // CREATE / UPDATE: O save() serve para ambos
    @Transactional
    public Aluno salvar(Aluno aluno) {
        // adicionar validações para verificar se o e-mail já existe
        if (alunoRepository.findByEmail(aluno.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Já existe um aluno cadastrado com este e-mail.");
        }
        return alunoRepository.save(aluno);
    }

    // READ: Busca todos os alunos
    public List<Aluno> listarTodos() {
        return alunoRepository.findAll();
    }

    // READ: Busca por ID para o Perfil
    public Optional<Aluno> buscarPorId(Long id) {
        return alunoRepository.findById(id);
    }

    // DELETE
    @Transactional
    public void excluir(Long id) {
        alunoRepository.deleteById(id);
    }
    
    // Método auxiliar para o login
    public Optional<Aluno> buscarPorEmail(String email) {
        return alunoRepository.findByEmail(email);
    }
}