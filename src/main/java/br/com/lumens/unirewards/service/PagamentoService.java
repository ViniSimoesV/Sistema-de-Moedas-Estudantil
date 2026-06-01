package br.com.lumens.unirewards.service;

import br.com.lumens.unirewards.dto.EmailTransacaoDTO;
import br.com.lumens.unirewards.dto.ResgateDTO;
import br.com.lumens.unirewards.model.*;
import br.com.lumens.unirewards.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PagamentoService {

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private CarteiraRepository carteiraRepository;

    @Autowired
    private VantagemRepository vantagemRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private EmailConsumer emailConsumer;

    @Transactional
    public Inventario resgatarVantagem(ResgateDTO dto) {
        // 1. Busca Aluno e Vantagem
        Aluno aluno = alunoRepository.findById(dto.getAlunoId())
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado no sistema."));
        
        Vantagem vantagem = vantagemRepository.findById(dto.getVantagemId())
                .orElseThrow(() -> new IllegalArgumentException("Vantagem não encontrada."));

        // 2. Busca e valida Carteira do Aluno
        Carteira carteira = carteiraRepository.findByUsuarioId(aluno.getId())
                .orElseThrow(() -> new IllegalArgumentException("Carteira não localizada."));

        if (!carteira.saldoSuficiente(vantagem.getCusto())) {
            throw new IllegalArgumentException("Saldo de Lúmens insuficiente para este resgate.");
        }

        // 3. Deduz o saldo e atualiza a carteira
        carteira.setSaldoAtual(carteira.getSaldoAtual() - vantagem.getCusto());
        carteiraRepository.save(carteira);

        // 4. Atualiza a contagem de resgates da Empresa e Unitario da Vantagem
        Empresa empresa = vantagem.getEmpresa();
        empresa.setTotalResgatados(empresa.getTotalResgatados() + 1);
        empresaRepository.save(empresa);
        vantagem.setQuantidadeResgates(vantagem.getQuantidadeResgates() + 1);
        vantagemRepository.save(vantagem);

        // 5. Registra o Pagamento (Histórico de transações da loja)
        Pagamento pagamento = new Pagamento();
        pagamento.setAluno(aluno);
        pagamento.setVantagem(vantagem);
        pagamento.setValorPago(vantagem.getCusto());
        pagamentoRepository.save(pagamento);

        // 6. Gera o Inventário (Cupom de uso do aluno)
        Inventario inventario = new Inventario();
        inventario.setAluno(aluno);
        inventario.setVantagem(vantagem);
        // O código do cupom é gerado automaticamente no @PrePersist da model Inventario
        inventario = inventarioRepository.save(inventario);

        // 7. Despacha os e-mails para a fila do RabbitMQ
        // E-mail para o Aluno (Com a confirmação e o cupom/QR Code)
        EmailTransacaoDTO emailAluno = new EmailTransacaoDTO();
        emailAluno.setEmailDestino(aluno.getEmail());
        emailAluno.setNomeDestino(aluno.getNome());
        emailAluno.setNomeOutraParte(empresa.getNome());
        emailAluno.setValor(vantagem.getCusto());
        // Enviando o código do cupom no motivo. No microserviço de e-mail, isso será transformado no QR Code
        emailAluno.setMotivo("Cupom: " + inventario.getCodigoCupom() + " | Vantagem: " + vantagem.getNome());
        emailAluno.setTipo("CUPOM_ALUNO"); 

        publicarEmailCupom(emailAluno);

        // E-mail para a Empresa (Aviso de que um produto foi resgatado)
        EmailTransacaoDTO emailEmpresa = new EmailTransacaoDTO();
        // Fallback temporário caso a empresa não possua um e-mail direto na entidade
        emailEmpresa.setEmailDestino(empresa.getNome().toLowerCase().replace(" ", "") + "@contato.com"); 
        emailEmpresa.setNomeDestino(empresa.getNome());
        emailEmpresa.setNomeOutraParte(aluno.getNome());
        emailEmpresa.setValor(vantagem.getCusto());
        emailEmpresa.setMotivo("Nova vantagem resgatada: " + vantagem.getNome());
        emailEmpresa.setTipo("AVISO_EMPRESA");

        publicarEmailCupom(emailEmpresa);

        return inventario; // Retornamos o inventário para que o front-end mostre o código na tela imediatamente
    }

    private void publicarEmailCupom(EmailTransacaoDTO email) {
        emailConsumer.processarEmail(email);
    }
}
