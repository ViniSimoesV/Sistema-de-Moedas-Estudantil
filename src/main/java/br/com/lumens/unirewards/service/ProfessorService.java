package br.com.lumens.unirewards.service;

import br.com.lumens.unirewards.dto.ProfessorDTO;
import br.com.lumens.unirewards.model.Carteira;
import br.com.lumens.unirewards.model.Instituicao;
import br.com.lumens.unirewards.model.Professor;
import br.com.lumens.unirewards.repository.InstituicaoRepository;
import br.com.lumens.unirewards.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import java.util.List;

@Service
public class ProfessorService {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private InstituicaoRepository instituicaoRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public Professor salvar(ProfessorDTO dto) {
        // 1. Valida se a instituição existe
        Instituicao instituicao = instituicaoRepository.findById(dto.getInstituicaoId())
                .orElseThrow(() -> new IllegalArgumentException("Instituição não encontrada no sistema!"));

        if (professorRepository.existsByCpf(dto.getCpf()) || professorRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Já existe um professor com este CPF ou E-mail.");
        }

        Professor professor = new Professor();
        
        // 2. Preenche os dados de Usuario
        professor.setNome(dto.getNome());
        professor.setSenha(passwordEncoder.encode("123")); // Senha padrão solicitada
        professor.setTipoUsuario("PROFESSOR");
        professor.setUrlFotoPerfil("");

        // 3. Preenche os dados de UsuarioAcademico
        professor.setCpf(dto.getCpf());
        professor.setEmail(dto.getEmail());
        professor.setInstituicao(instituicao);

        // 4. Preenche os dados de Professor
        professor.setDepartamento(dto.getDepartamento());

        // 5. Criação da Carteira Inicial
        Carteira carteira = new Carteira();
        // Nota: Substitua "setSaldoAtual" pelo nome exato que estiver na sua classe Carteira.java
        carteira.setSaldoAtual(1000); 
        carteira.setUsuario(professor); // Faz a ligação com o dono da carteira
        
        // Como você usou CascadeType.ALL na model, ao salvar o professor, a carteira salva automaticamente!
        professor.setCarteira(carteira); 

        return professorRepository.save(professor);
    }

    @Transactional
    public Professor atualizar(Long id, ProfessorDTO dto) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado!"));

        // Verifica se o CPF ou Email foram alterados para um que já existe no banco
        if (!professor.getCpf().equals(dto.getCpf()) && professorRepository.existsByCpf(dto.getCpf())) {
            throw new IllegalArgumentException("Já existe outro professor com este CPF.");
        }
        if (!professor.getEmail().equals(dto.getEmail()) && professorRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Já existe outro professor com este E-mail.");
        }

        professor.setNome(dto.getNome());
        professor.setCpf(dto.getCpf());
        professor.setEmail(dto.getEmail());
        professor.setDepartamento(dto.getDepartamento());

        return professorRepository.save(professor);
    }

    // Método para excluir o professor pelo ID
    @Transactional
    public void excluir(Long id) {
        if (professorRepository.existsById(id)) {
            professorRepository.deleteById(id);
        }
    }

    public List<Professor> listarPorInstituicao(Long instituicaoId) {
        return professorRepository.findByInstituicaoId(instituicaoId);
    }

    // Método para ler o CSV e cadastrar em lote
    @Transactional
    public List<Professor> processarCsv(MultipartFile file, Long instituicaoId) throws Exception {
        List<Professor> salvos = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"))) {
            String linha;
            boolean primeiraLinha = true;
            
            while ((linha = br.readLine()) != null) {
                // Pula a primeira linha se for o cabeçalho
                if (primeiraLinha) {
                    primeiraLinha = false;
                    if (linha.toLowerCase().contains("nome")) continue; 
                }
                
                // Divide as colunas por vírgula ou ponto-e-vírgula
                String[] colunas = linha.split("[,;]");
                
                if (colunas.length >= 4) {
                    ProfessorDTO dto = new ProfessorDTO();
                    dto.setNome(colunas[0].trim());
                    dto.setCpf(colunas[1].trim());
                    dto.setEmail(colunas[2].trim());
                    dto.setDepartamento(colunas[3].trim());
                    dto.setInstituicaoId(instituicaoId);
                    
                    try {
                        // Reutiliza a sua regra de negócio perfeita do cadastro manual!
                        salvos.add(this.salvar(dto));
                    } catch (IllegalArgumentException e) {
                        // Se o professor já existir (CPF/Email duplicado), ele ignora esta linha e continua o resto
                        System.out.println("Linha ignorada (duplicado): " + dto.getNome());
                    }
                }
            }
        }
        return salvos;
    }
}