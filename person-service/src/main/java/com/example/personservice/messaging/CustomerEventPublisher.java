package com.example.personservice.messaging;

import com.example.personservice.config.RabbitMQConfig;
import com.example.personservice.entity.Customer;
import com.example.personservice.event.CustomerEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishEvent(String action, Customer customer) {
        CustomerEventDTO event = new CustomerEventDTO(
                action,
                customer.getCustomerId(),
                customer.getName(),
                customer.getStatus()
        );
        log.info("Publishing '{}' event to RabbitMQ: {}", action, event);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.CUSTOMER_EXCHANGE,
                RabbitMQConfig.CUSTOMER_ROUTING_KEY,
                event
        );
    }
}
