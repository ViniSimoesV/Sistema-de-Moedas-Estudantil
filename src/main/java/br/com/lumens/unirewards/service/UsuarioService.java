package br.com.lumens.unirewards.service;

import br.com.lumens.unirewards.model.Aluno;
import br.com.lumens.unirewards.model.Empresa;
import br.com.lumens.unirewards.model.Professor;
import br.com.lumens.unirewards.model.Usuario;
import br.com.lumens.unirewards.repository.AlunoRepository;
import br.com.lumens.unirewards.repository.EmpresaRepository;
import br.com.lumens.unirewards.repository.ProfessorRepository;
import br.com.lumens.unirewards.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; 
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private AlunoRepository alunoRepository; // Para buscar por e-mail

    @Autowired
    private ProfessorRepository professorRepository; // Para buscar por e-mail

    @Autowired
    private EmpresaRepository empresaRepository; // Para buscar por CNPJ

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Optional<Usuario> realizarLogin(String identificador, String senha) {
        // 1. Tenta encontrar o e-mail na tabela de Alunos
        Optional<Aluno> alunoOpt = alunoRepository.findByEmail(identificador);
        if (alunoOpt.isPresent()) {
            Aluno aluno = alunoOpt.get();
            if (passwordEncoder.matches(senha, aluno.getSenha())) {
                return Optional.of(aluno);
            }
        }

        // 2. Tenta encontrar o e-mail na tabela de Professores
        Optional<Professor> professorOpt = professorRepository.findByEmail(identificador);
        if (professorOpt.isPresent()) {
            Professor professor = professorOpt.get();
            if (passwordEncoder.matches(senha, professor.getSenha())) {
                return Optional.of(professor); 
            }
        }

        // 3. Se não achou, tenta buscar em Empresas pelo CNPJ
        Optional<Empresa> empresa = empresaRepository.findByCnpj(identificador);
        if (empresa.isPresent() && passwordEncoder.matches(senha, empresa.get().getSenha())) {
            return Optional.of(empresa.get());
        }

        return Optional.empty();
    }
}