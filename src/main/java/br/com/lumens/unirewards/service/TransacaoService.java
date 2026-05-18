package br.com.lumens.unirewards.service;

import br.com.lumens.unirewards.dto.TransacaoRequestDTO;
import br.com.lumens.unirewards.model.*;
import br.com.lumens.unirewards.repository.*;
import br.com.lumens.unirewards.repository.TransacaoProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class TransacaoService {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private CarteiraRepository carteiraRepository;

    @Autowired
    private TransacaoAlunoRepository transacaoAlunoRepository;

    @Autowired
    private TransacaoProfessorRepository transacaoProfessorRepository;

    @Transactional
    public void processarTransferencia(TransacaoRequestDTO dto) {
        // 1. Validação básica de valor lógico
        if (dto.getValor() == null || dto.getValor() <= 0) {
            throw new IllegalArgumentException("A quantidade de Lúmens deve ser maior do que zero.");
        }

        // 2. Busca e valida o Aluno Destinatário (Comum para ambos os fluxos)
        Aluno alunoDestinatario = alunoRepository.findById(dto.getDestinatarioId())
                .orElseThrow(() -> new IllegalArgumentException("Aluno destinatário não localizado no sistema."));

        // 3. SEPARAÇÃO DE FLUXOS: Quem está enviando?
        if ("PROFESSOR".equalsIgnoreCase(dto.getTipoRemetente())) {
            
            // FLUXO PROFESSOR -> ALUNO
            Professor professorRemetente = professorRepository.findById(dto.getRemetenteId())
                    .orElseThrow(() -> new IllegalArgumentException("Professor remetente não localizado."));

            Carteira carteiraProfessor = carteiraRepository.findByUsuarioId(professorRemetente.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Carteira do professor não localizada."));

            // Valida limite de saldo atual
            if (!carteiraProfessor.saldoSuficiente(dto.getValor())) {
                throw new IllegalArgumentException("Saldo insuficiente na carteira de distribuição.");
            }

            // Executa a transferência de valores nas carteiras
            carteiraProfessor.setSaldoAtual(carteiraProfessor.getSaldoAtual() - dto.getValor());
            
            Carteira carteiraAluno = carteiraRepository.findByUsuarioId(alunoDestinatario.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Carteira do aluno destinatário não localizada."));
            carteiraAluno.setSaldoAtual(carteiraAluno.getSaldoAtual() + dto.getValor());

            // Salva os novos saldos atualizados
            carteiraRepository.save(carteiraProfessor);
            carteiraRepository.save(carteiraAluno);

            // Registra o histórico na tabela transacoes_professores
            TransacaoProfessor logProfessor = new TransacaoProfessor();
            logProfessor.setProfessor(professorRemetente);
            logProfessor.setAluno(alunoDestinatario);
            logProfessor.setValor(dto.getValor());
            logProfessor.setMensagem(dto.getMotivo());
            transacaoProfessorRepository.save(logProfessor);

        } else if ("ALUNO".equalsIgnoreCase(dto.getTipoRemetente())) {
            
            // FLUXO ALUNO -> ALUNO (Peer-to-Peer)
            if (dto.getRemetenteId().equals(dto.getDestinatarioId())) {
                throw new IllegalArgumentException("Operação inválida: Não é permitido transferir Lúmens para si mesmo.");
            }

            Aluno alunoRemetente = alunoRepository.findById(dto.getRemetenteId())
                    .orElseThrow(() -> new IllegalArgumentException("Aluno remetente não localizado."));

            Carteira carteiraRemetente = carteiraRepository.findByUsuarioId(alunoRemetente.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Sua carteira acadêmica não foi localizada."));

            // Valida limite de saldo atual
            if (!carteiraRemetente.saldoSuficiente(dto.getValor())) {
                throw new IllegalArgumentException("Saldo de Lúmens insuficiente para concluir o envio.");
            }

            // Executa a transferência de valores nas carteiras
            carteiraRemetente.setSaldoAtual(carteiraRemetente.getSaldoAtual() - dto.getValor());
            
            Carteira carteiraDestinatario = carteiraRepository.findByUsuarioId(alunoDestinatario.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Carteira do aluno destinatário não localizada."));
            carteiraDestinatario.setSaldoAtual(carteiraDestinatario.getSaldoAtual() + dto.getValor());

            // Salva os novos saldos atualizados
            carteiraRepository.save(carteiraRemetente);
            carteiraRepository.save(carteiraDestinatario);

            // Registra o histórico na tabela pagamentos (Aluno para Aluno)
            TransacaoAluno logAluno = new TransacaoAluno();
            logAluno.setRemetente(alunoRemetente);
            logAluno.setDestinatario(alunoDestinatario);
            logAluno.setValor(dto.getValor());
            logAluno.setMotivo(dto.getMotivo());
            transacaoAlunoRepository.save(logAluno);

        } else {
            throw new IllegalArgumentException("Tipo de perfil remetente não suportado para transferências.");
        }
    }
}