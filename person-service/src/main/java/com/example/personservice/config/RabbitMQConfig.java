package com.example.personservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    public static final String CUSTOMER_QUEUE = "customer.queue";
    public static final String CUSTOMER_EXCHANGE = "customer.exchange";
    public static final String CUSTOMER_ROUTING_KEY = "customer.routing.key";


    @Bean
    public Queue customerQueue() {
        return QueueBuilder.durable(CUSTOMER_QUEUE).build();
    }

    @Bean
    public DirectExchange customerExchange() {
        return new DirectExchange(CUSTOMER_EXCHANGE);
    }


    @Bean
    public Binding customerBinding(Queue customerQueue, DirectExchange customerExchange) {
        return BindingBuilder
                .bind(customerQueue)
                .to(customerExchange)
                .with(CUSTOMER_ROUTING_KEY);
    }


    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
