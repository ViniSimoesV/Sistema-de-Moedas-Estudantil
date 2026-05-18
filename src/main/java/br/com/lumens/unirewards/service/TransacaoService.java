package br.com.lumens.unirewards.service;

import br.com.lumens.unirewards.config.RabbitMQConfig;
import br.com.lumens.unirewards.dto.EmailTransacaoDTO;
import br.com.lumens.unirewards.dto.TransacaoRequestDTO;
import br.com.lumens.unirewards.model.*;
import br.com.lumens.unirewards.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Autowired
    private org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate;

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

            // --- INÍCIO DO DESPACHO RABBITMQ (PROFESSOR -> ALUNO) ---
            
            // 1. Despacha e-mail para o Aluno (Recebeu)
            EmailTransacaoDTO emailAluno = new EmailTransacaoDTO();
            emailAluno.setEmailDestino(alunoDestinatario.getEmail());
            emailAluno.setNomeDestino(alunoDestinatario.getNome());
            emailAluno.setNomeOutraParte("Prof. " + professorRemetente.getNome());
            emailAluno.setValor(dto.getValor());
            emailAluno.setMotivo(dto.getMotivo());
            emailAluno.setTipo("RECEBIDO");
            rabbitTemplate.convertAndSend(RabbitMQConfig.FILA_EMAILS_TRANSACOES, emailAluno);

            // 2. Despacha e-mail para o Professor (Enviou)
            EmailTransacaoDTO emailProf = new EmailTransacaoDTO();
            emailProf.setEmailDestino(professorRemetente.getEmail());
            emailProf.setNomeDestino(professorRemetente.getNome());
            emailProf.setNomeOutraParte(alunoDestinatario.getNome());
            emailProf.setValor(dto.getValor());
            emailProf.setMotivo(dto.getMotivo());
            emailProf.setTipo("ENVIADO");
            rabbitTemplate.convertAndSend(RabbitMQConfig.FILA_EMAILS_TRANSACOES, emailProf);
            
            // --- FIM DO DESPACHO ---

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

            // --- INÍCIO DO DESPACHO RABBITMQ (ALUNO -> ALUNO) ---
            
            // 1. Despacha e-mail para o Aluno Destinatário (Recebeu)
            EmailTransacaoDTO emailRecebedor = new EmailTransacaoDTO();
            emailRecebedor.setEmailDestino(alunoDestinatario.getEmail());
            emailRecebedor.setNomeDestino(alunoDestinatario.getNome());
            emailRecebedor.setNomeOutraParte(alunoRemetente.getNome());
            emailRecebedor.setValor(dto.getValor());
            emailRecebedor.setMotivo(dto.getMotivo());
            emailRecebedor.setTipo("RECEBIDO");
            rabbitTemplate.convertAndSend(RabbitMQConfig.FILA_EMAILS_TRANSACOES, emailRecebedor);

            // 2. Despacha e-mail para o Aluno Remetente (Enviou)
            EmailTransacaoDTO emailRemetente = new EmailTransacaoDTO();
            emailRemetente.setEmailDestino(alunoRemetente.getEmail());
            emailRemetente.setNomeDestino(alunoRemetente.getNome());
            emailRemetente.setNomeOutraParte(alunoDestinatario.getNome());
            emailRemetente.setValor(dto.getValor());
            emailRemetente.setMotivo(dto.getMotivo());
            emailRemetente.setTipo("ENVIADO");
            rabbitTemplate.convertAndSend(RabbitMQConfig.FILA_EMAILS_TRANSACOES, emailRemetente);

            // --- FIM DO DESPACHO ---

        } else {
            throw new IllegalArgumentException("Tipo de perfil remetente não suportado para transferências.");
        }
    }

    public List<TransacaoProfessor> listarExtratoProfessor(Long professorId) {
        return transacaoProfessorRepository.findByProfessorIdOrderByDataEnvioDesc(professorId);
    }

    public java.util.Map<String, Object> listarExtratoAluno(Long alunoId) {
        List<TransacaoProfessor> recebidosProfessores = transacaoProfessorRepository.findByAlunoIdOrderByDataEnvioDesc(alunoId);
        List<TransacaoAluno> enviadosParaAlunos = transacaoAlunoRepository.findByRemetenteIdOrderByDataEnvioDesc(alunoId);
        List<TransacaoAluno> recebidosDeAlunos = transacaoAlunoRepository.findByDestinatarioIdOrderByDataEnvioDesc(alunoId);

        java.util.Map<String, Object> extratoCompleto = new java.util.HashMap<>();
        extratoCompleto.put("recebidosProfessores", recebidosProfessores);
        extratoCompleto.put("enviadosAlunos", enviadosParaAlunos);
        extratoCompleto.put("recebidosAlunos", recebidosDeAlunos);
        return extratoCompleto;
    }
}