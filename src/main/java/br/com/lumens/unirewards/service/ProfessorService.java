package br.com.lumens.unirewards.service;

import br.com.lumens.unirewards.dto.ProfessorDTO;
import br.com.lumens.unirewards.model.Carteira;
import br.com.lumens.unirewards.model.Instituicao;
import br.com.lumens.unirewards.model.Professor;
import br.com.lumens.unirewards.repository.CarteiraRepository;
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
import java.util.Optional;

@Service
public class ProfessorService {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private InstituicaoRepository instituicaoRepository;

    @Autowired
    private CarteiraRepository carteiraRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public Professor salvar(ProfessorDTO dto) {
        Instituicao instituicao = instituicaoRepository.findById(dto.getInstituicaoId())
                .orElseThrow(() -> new IllegalArgumentException("Instituição não encontrada no sistema!"));

        if (professorRepository.existsByCpf(dto.getCpf()) || professorRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Já existe um professor com este CPF ou E-mail.");
        }

        Professor professor = new Professor();
        
        professor.setNome(dto.getNome());
        professor.setSenha(passwordEncoder.encode("123")); 
        professor.setTipoUsuario("PROFESSOR");
        
        // Verifica se a foto foi enviada, senão coloca vazio
        if (dto.getUrlFotoPerfil() != null) {
            professor.setUrlFotoPerfil(dto.getUrlFotoPerfil());
        } else {
            professor.setUrlFotoPerfil("");
        }

        professor.setCpf(dto.getCpf());
        professor.setEmail(dto.getEmail());
        professor.setInstituicao(instituicao);
        professor.setDepartamento(dto.getDepartamento());

        // Salva o professor PRIMEIRO. Isso força o banco a gerar o usuario_id.
        Professor professorSalvo = professorRepository.save(professor);

        // DEPOIS, cria e vincula a carteira ao professor salvo
        Carteira carteira = new Carteira();
        carteira.setSaldoAtual(1000); 
        carteira.setUsuario(professorSalvo); 
        
        // Salva a carteira de forma explícita e direta na tabela
        carteiraRepository.save(carteira);
        
        // Atualiza o objeto para retornar bonitinho
        professorSalvo.setCarteira(carteira);

        return professorSalvo;
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

        // Só atualiza a foto se for enviada uma nova
        if (dto.getUrlFotoPerfil() != null && !dto.getUrlFotoPerfil().isEmpty()) {
            professor.setUrlFotoPerfil(dto.getUrlFotoPerfil());
        }

        // SALVA as alterações do professor
        Professor professorSalvo = professorRepository.save(professor);

        // BUSCA de forma explícita a carteira associada para garantir o vínculo no retorno do JSON
        Optional<Carteira> carteiraOpt = carteiraRepository.findByUsuarioId(id);
        if (carteiraOpt.isPresent()) {
            professorSalvo.setCarteira(carteiraOpt.get());
        } else {
            // Fallback de segurança: se por algum motivo bizarro o professor não tiver carteira, cria uma
            Carteira novaCarteira = new Carteira();
            novaCarteira.setSaldoAtual(0);
            novaCarteira.setUsuario(professorSalvo);
            carteiraRepository.save(novaCarteira);
            professorSalvo.setCarteira(novaCarteira);
        }

        return professorSalvo;
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

    public Optional<Professor> buscarPorId(Long id) {
        Optional<Professor> professorOpt = professorRepository.findById(id);
    
        // Se o professor existir, buscamos a carteira dele no banco e associamos explicitamente
        professorOpt.ifPresent(professor -> {
            carteiraRepository.findByUsuarioId(id).ifPresent(carteira -> {
                professor.setCarteira(carteira);
            });
        });
        
        return professorOpt;
    }
}