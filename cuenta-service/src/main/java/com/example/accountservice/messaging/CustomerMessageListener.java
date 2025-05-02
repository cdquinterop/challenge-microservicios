package com.example.accountservice.messaging;

import com.example.accountservice.config.RabbitMQConfig;
import com.example.accountservice.dto.event.CustomerEventDTO;
import com.example.accountservice.entity.Customer;
import com.example.accountservice.entity.Account;
import com.example.accountservice.repository.ClienteRepository;
import com.example.accountservice.repository.CuentaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClienteMessageListener {

    private final ClienteRepository clienteRepository;
    private final CuentaRepository cuentaRepository;

    @RabbitListener(queues = RabbitMQConfig.CLIENTE_QUEUE)
    public void handleClienteEvent(CustomerEventDTO event) {
        log.info(" Evento recibido desde RabbitMQ: {}", event);

        String action = event.getAction().toUpperCase();
        switch (action) {
            case "CREATE" -> procesarCreacion(event);
            case "UPDATE" -> procesarActualizacion(event);
            case "DELETE" -> procesarEliminacion(event);
            default -> log.warn("⚠️ Acción no reconocida: {}", action);
        }
    }

    @Transactional
    protected void procesarCreacion(CustomerEventDTO event) {
        Customer customer = mapToEntity(event);
        clienteRepository.save(customer);
        log.info(" Cliente creado: {}", customer);
    }

    @Transactional
    protected void procesarActualizacion(CustomerEventDTO event) {
        Customer customer = clienteRepository.findByClienteId(event.getClienteId())
                .orElseGet(() -> new Customer());

        customer.setClienteId(event.getClienteId());
        customer.setNombre(event.getNombre());
        customer.setEstado(event.getEstado());

        clienteRepository.save(customer);
        log.info(" Cliente actualizado: {}", customer);
    }

    @Transactional
    protected void procesarEliminacion(CustomerEventDTO event) {
        Customer customer = clienteRepository.findByClienteId(event.getClienteId())
                .orElse(null);

        if (customer != null) {
            customer.setEstado(false);
            clienteRepository.save(customer);
            log.info(" Cliente desactivado: {}", customer);
        }

        List<Account> accounts = cuentaRepository.findAllByClienteId(event.getClienteId());
        accounts.forEach(cuenta -> {
            cuenta.setEstado(false);
            cuentaRepository.save(cuenta);
        });

        log.info(" Se desactivaron {} cuentas asociadas al cliente {}", accounts.size(), event.getClienteId());
    }

    private Customer mapToEntity(CustomerEventDTO dto) {
        Customer customer = new Customer();
        customer.setClienteId(dto.getClienteId());
        customer.setNombre(dto.getNombre());
        customer.setEstado(dto.getEstado());
        return customer;
    }
}
