package br.com.lumens.unirewards.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String FILA_EMAILS_TRANSACOES = "emails.transacoes.fila";

    @Bean
    public Queue filaEmails() {
        return new Queue(FILA_EMAILS_TRANSACOES, true);
    }

    @Bean
    public MessageConverter messageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Previne erros de conversão com LocalDateTime
        return new Jackson2JsonMessageConverter(mapper);
    }
}