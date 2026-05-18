package br.com.lumens.unirewards.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String FILA_EMAILS_TRANSACOES = "emails.transacoes.fila";

    // Cria a fila no servidor do RabbitMQ se ela não existir
    @Bean
    public Queue filaEmails() {
        return new Queue(FILA_EMAILS_TRANSACOES, true);
    }

    // Converte os nossos objetos Java para JSON na fila
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}