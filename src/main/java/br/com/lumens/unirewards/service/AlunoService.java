package br.com.lumens.unirewards.service;

import br.com.lumens.unirewards.dto.AlunoDTO;
import br.com.lumens.unirewards.model.Aluno;
import br.com.lumens.unirewards.model.Carteira;
import br.com.lumens.unirewards.model.Instituicao;
import br.com.lumens.unirewards.repository.AlunoRepository;
import br.com.lumens.unirewards.repository.InstituicaoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AlunoService {

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private InstituicaoRepository instituicaoRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // CREATE / UPDATE: O save() serve para ambos
    @Transactional
    public Aluno salvar(AlunoDTO dto) {
        Aluno aluno = new Aluno();
        
        // Dados de Usuario (Classe avó)
        aluno.setNome(dto.getNome());
        
        // CRIPTOGRAFIA: Transforma "senha123" em um hash seguro
        String senhaCriptografada = passwordEncoder.encode(dto.getSenha());
        aluno.setSenha(senhaCriptografada);
        
        aluno.setUrlFotoPerfil(dto.getUrlFotoPerfil());
        aluno.setTipoUsuario("ALUNO");

        // Dados de UsuarioAcademico (Classe pai)
        aluno.setCpf(dto.getCpf());
        aluno.setEmail(dto.getEmail()); 

        // Dados específicos de Aluno
        aluno.setRg(dto.getRg());
        aluno.setCurso(dto.getCurso());
        aluno.setNivel(1); 

        // Busca a instituição pelo ID enviado pelo front-end
        Instituicao inst = instituicaoRepository.findById(dto.getInstituicaoId())
            .orElseThrow(() -> new RuntimeException("Instituição não encontrada"));
        
        aluno.setInstituicao(inst);
        
        // Endereço
        aluno.setRua(dto.getRua());
        aluno.setNumero(dto.getNumero());
        aluno.setComplemento(dto.getComplemento());
        aluno.setBairro(dto.getBairro());
        aluno.setCidade(dto.getCidade());

        // CRIAÇÃO AUTOMÁTICA DA CARTEIRA
        Carteira carteira = new Carteira();
        carteira.setSaldoAtual(0);
        carteira.setUsuario(aluno); // Vincula a carteira ao aluno
        aluno.setCarteira(carteira);

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

    // UPDATE: Atualiza um aluno existente
    @Transactional
    public Aluno atualizar(Long id, AlunoDTO dto) {
        // 1. Busca o aluno existente ou lança erro se não achar
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        // 2. Atualiza apenas os campos permitidos
        aluno.setNome(dto.getNome());
        if (dto.getSenha() != null && !dto.getSenha().isEmpty()) {
            aluno.setSenha(passwordEncoder.encode(dto.getSenha()));
        }
        

        aluno.setNome(dto.getNome());
        aluno.setEmail(dto.getEmail());
        aluno.setRg(dto.getRg());
        aluno.setUrlFotoPerfil(dto.getUrlFotoPerfil());
        aluno.setCurso(dto.getCurso());
        aluno.setNivel(dto.getNivel());
        aluno.setRua(dto.getRua());
        aluno.setNumero(dto.getNumero());
        aluno.setComplemento(dto.getComplemento());
        aluno.setBairro(dto.getBairro());
        aluno.setCidade(dto.getCidade());


        return alunoRepository.save(aluno);
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