package br.com.lumens.unirewards.service;

import br.com.lumens.unirewards.config.RabbitMQConfig;
import br.com.lumens.unirewards.dto.EmailTransacaoDTO;
import jakarta.mail.internet.MimeMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailConsumer {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private QrCodeService qrCodeService;

    @RabbitListener(queues = RabbitMQConfig.FILA_EMAILS_CUPONS)
    public void processarEmail(EmailTransacaoDTO emailDto) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(emailDto.getEmailDestino());
            helper.setFrom("unirewards.suporte@gmail.com", "UniRewards");
            helper.setSubject("🏷️ UniRewards | Comprovante de Resgate");
            
            // Gera o QR Code com o código que está dentro do motivo (configurado no PagamentoService)
            byte[] qrCodeBytes = qrCodeService.gerarQrCodeImage(emailDto.getMotivo(), 200, 200);

            // Template Visual Alinhado com o EmailService
            String htmlContent = 
                "<div style=\"font-family: 'Arial', sans-serif; background-color: #0D1B2A; color: #FFFFFF; padding: 40px 20px; text-align: center;\">"
                + "    <div style=\"max-width: 500px; margin: 0 auto; background-color: #1B263B; padding: 30px; border-radius: 12px; border: 1px solid rgba(255, 215, 0, 0.2);\">"
                + "        <h1 style=\"color: #FFD700; margin-bottom: 5px;\">UniRewards</h1>"
                + "        <p style=\"color: #A9B2C3; font-size: 14px; margin-top: 0;\">Comprovante de Resgate</p>"
                + "        <hr style=\"border-color: rgba(255, 255, 255, 0.1); margin: 20px 0;\">"
                + "        <h2 style=\"color: #FFFFFF;\">Olá, " + emailDto.getNomeDestino() + "</h2>"
                + "        <p style=\"color: #ECEFF4; line-height: 1.6;\">Sua vantagem foi resgatada com sucesso!</p>"
                + "        <div style=\"background-color: rgba(255,255,255,0.05); padding: 20px; border-radius: 8px; margin: 25px 0;\">"
                + "            <h3 style=\"color: #FFD700; margin: 0;\">" + emailDto.getMotivo() + "</h3>"
                + "        </div>"
                + "        <div style=\"background-color: white; display: inline-block; padding: 10px; border-radius: 8px; margin-bottom: 20px;\">"
                + "            <img src='cid:qrcodeImage' style='display: block;' />"
                + "        </div>"
                + "        <p style=\"color: #8A95A5; font-size: 12px;\">Apresente este QR Code no estabelecimento parceiro.</p>"
                + "    </div>"
                + "</div>";

            helper.setText(htmlContent, true);
            helper.addInline("qrcodeImage", new ByteArrayResource(qrCodeBytes), "image/png");

            mailSender.send(message);
            System.out.println("E-mail estilizado enviado com sucesso para: " + emailDto.getEmailDestino());
            
        } catch (Exception e) {
            System.err.println("Erro ao processar e-mail de resgate: " + e.getMessage());
            e.printStackTrace();
        }
    }
}