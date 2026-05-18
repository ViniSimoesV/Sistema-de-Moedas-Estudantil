package br.com.lumens.unirewards.service;

import br.com.lumens.unirewards.config.RabbitMQConfig;
import br.com.lumens.unirewards.dto.EmailTransacaoDTO;
import jakarta.mail.internet.MimeMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Fica escutando a fila assincronamente
    @RabbitListener(queues = RabbitMQConfig.FILA_EMAILS_TRANSACOES)
    public void processarEmailTransacao(EmailTransacaoDTO dto) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(dto.getEmailDestino());
            
            String assunto = dto.getTipo().equals("RECEBIDO") 
                ? "✨ Você recebeu novos Lúmens!" 
                : "✅ Transferência de Lúmens confirmada!";
            helper.setSubject(assunto);

            // Template HTML com a estética UniRewards (Dark Navy e Lumen Gold)
            String htmlTemplate = montarTemplateHtml(dto);
            helper.setText(htmlTemplate, true);

            mailSender.send(message);
            System.out.println("E-mail enviado com sucesso para: " + dto.getEmailDestino());

        } catch (Exception e) {
            System.err.println("Erro ao processar e-mail da fila: " + e.getMessage());
        }
    }

    private String montarTemplateHtml(EmailTransacaoDTO dto) {
        String corDestaque = dto.getTipo().equals("RECEBIDO") ? "#52B788" : "#FFD700";
        String icone = dto.getTipo().equals("RECEBIDO") ? "+" : "-";
        String acaoTexto = dto.getTipo().equals("RECEBIDO") 
            ? "Você recebeu uma transferência de <b>" + dto.getNomeOutraParte() + "</b>."
            : "Você enviou Lúmens para <b>" + dto.getNomeOutraParte() + "</b>.";

        return "<div style=\"font-family: 'Arial', sans-serif; background-color: #0D1B2A; color: #FFFFFF; padding: 40px 20px; text-align: center;\">"
             + "    <div style=\"max-width: 500px; margin: 0 auto; background-color: #1B263B; padding: 30px; border-radius: 12px; border: 1px solid rgba(255, 215, 0, 0.2);\">"
             + "        <h1 style=\"color: #FFD700; margin-bottom: 5px;\">UniRewards</h1>"
             + "        <p style=\"color: #A9B2C3; font-size: 14px; margin-top: 0;\">Comprovante de Movimentação</p>"
             + "        <hr style=\"border-color: rgba(255, 255, 255, 0.1); margin: 20px 0;\">"
             + "        <h2 style=\"color: #FFFFFF;\">Olá, " + dto.getNomeDestino() + "</h2>"
             + "        <p style=\"color: #ECEFF4; line-height: 1.6;\">" + acaoTexto + "</p>"
             + "        <div style=\"background-color: rgba(255,255,255,0.05); padding: 20px; border-radius: 8px; margin: 25px 0;\">"
             + "            <span style=\"font-size: 32px; font-weight: bold; color: " + corDestaque + ";\">" + icone + " " + dto.getValor() + " Lúmens</span>"
             + "            <p style=\"color: #A9B2C3; font-style: italic; margin-top: 15px; font-size: 14px;\">\"" + dto.getMotivo() + "\"</p>"
             + "        </div>"
             + "        <p style=\"color: #8A95A5; font-size: 12px;\">Acesse seu extrato na plataforma para ver todo o seu histórico.</p>"
             + "    </div>"
             + "</div>";
    }
}