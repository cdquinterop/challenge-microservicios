package com.example.cuentaservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String CLIENTE_QUEUE = "cliente.queue";
    public static final String CLIENTE_EXCHANGE = "cliente.exchange";
    public static final String CLIENTE_ROUTING_KEY = "cliente.routing.key";

    @Bean
    public Queue clienteQueue() {
        return QueueBuilder.durable(CLIENTE_QUEUE).build();
    }

    @Bean
    public DirectExchange clienteExchange() {
        return new DirectExchange(CLIENTE_EXCHANGE);
    }

    @Bean
    public Binding clienteBinding(Queue clienteQueue, DirectExchange clienteExchange) {
        return BindingBuilder
                .bind(clienteQueue)
                .to(clienteExchange)
                .with(CLIENTE_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());

        template.setMandatory(true);
        template.setBeforePublishPostProcessors(message -> {
            message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return message;
        });

        return template;
    }
}
