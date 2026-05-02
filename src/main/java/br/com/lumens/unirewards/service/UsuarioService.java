package br.com.lumens.unirewards.service;

import br.com.lumens.unirewards.model.Aluno;
import br.com.lumens.unirewards.model.Empresa;
import br.com.lumens.unirewards.model.Usuario;
import br.com.lumens.unirewards.repository.AlunoRepository;
import br.com.lumens.unirewards.repository.EmpresaRepository;
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
    private EmpresaRepository empresaRepository; // Para buscar por CNPJ

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Optional<Usuario> realizarLogin(String identificador, String senha) {
        // 1. Tenta buscar em Alunos pelo e-mail
        Optional<Aluno> aluno = alunoRepository.findByEmail(identificador);
        // Usa passwordEncoder.matches(senha_pura, senha_do_banco)
        if (aluno.isPresent() && passwordEncoder.matches(senha, aluno.get().getSenha())) {
            return Optional.of(aluno.get());
        }

        // 2. Se não achou, tenta buscar em Empresas pelo CNPJ
        Optional<Empresa> empresa = empresaRepository.findByCnpj(identificador);
        if (empresa.isPresent() && passwordEncoder.matches(senha, empresa.get().getSenha())) {
            return Optional.of(empresa.get());
        }

        return Optional.empty();
    }
}