package br.com.lumens.unirewards.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String FILA_EMAILS_TRANSACOES = "emails.transacoes.fila";
    public static final String FILA_EMAILS_CUPONS = "emails.cupons.fila";

    @Bean
    public Queue filaEmails() {
        return new Queue(FILA_EMAILS_TRANSACOES, true);
    }

    @Bean
    public Queue filaCupons() {
        return new Queue(FILA_EMAILS_CUPONS, true);
    }

    @Bean
    public MessageConverter messageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        // Configure os outros campos aqui (host, user, etc.)
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        connectionFactory.setPublisherReturns(true);
        return connectionFactory;
    }
}