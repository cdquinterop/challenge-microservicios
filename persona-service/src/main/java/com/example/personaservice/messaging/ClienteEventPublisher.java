package com.example.personaservice.messaging;

import com.example.personaservice.config.RabbitMQConfig;
import com.example.personaservice.entity.Cliente;
import com.example.personaservice.event.ClienteEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClienteEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publicarEvento(String action, Cliente cliente) {
        ClienteEventDTO event = new ClienteEventDTO(
                action,
                cliente.getClienteId(),
                cliente.getNombre(),
                cliente.getEstado()
        );
        log.info("📤 Publicando evento '{}' a RabbitMQ: {}", action, event);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.CLIENTE_EXCHANGE,
                RabbitMQConfig.CLIENTE_ROUTING_KEY,
                event
        );
    }

}
