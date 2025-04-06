package com.example.cuentaservice.messaging;

import com.example.cuentaservice.config.RabbitMQConfig;
import com.example.cuentaservice.dto.event.ClienteEventDTO;
import com.example.cuentaservice.entity.Cliente;
import com.example.cuentaservice.entity.Cuenta;
import com.example.cuentaservice.repository.ClienteRepository;
import com.example.cuentaservice.repository.CuentaRepository;
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
    public void handleClienteEvent(ClienteEventDTO event) {
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
    protected void procesarCreacion(ClienteEventDTO event) {
        Cliente cliente = mapToEntity(event);
        clienteRepository.save(cliente);
        log.info(" Cliente creado: {}", cliente);
    }

    @Transactional
    protected void procesarActualizacion(ClienteEventDTO event) {
        Cliente cliente = clienteRepository.findByClienteId(event.getClienteId())
                .orElseGet(() -> new Cliente());

        cliente.setClienteId(event.getClienteId());
        cliente.setNombre(event.getNombre());
        cliente.setEstado(event.getEstado());

        clienteRepository.save(cliente);
        log.info(" Cliente actualizado: {}", cliente);
    }

    @Transactional
    protected void procesarEliminacion(ClienteEventDTO event) {
        Cliente cliente = clienteRepository.findByClienteId(event.getClienteId())
                .orElse(null);

        if (cliente != null) {
            cliente.setEstado(false);
            clienteRepository.save(cliente);
            log.info(" Cliente desactivado: {}", cliente);
        }

        List<Cuenta> cuentas = cuentaRepository.findAllByClienteId(event.getClienteId());
        cuentas.forEach(cuenta -> {
            cuenta.setEstado(false);
            cuentaRepository.save(cuenta);
        });

        log.info(" Se desactivaron {} cuentas asociadas al cliente {}", cuentas.size(), event.getClienteId());
    }

    private Cliente mapToEntity(ClienteEventDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setClienteId(dto.getClienteId());
        cliente.setNombre(dto.getNombre());
        cliente.setEstado(dto.getEstado());
        return cliente;
    }
}
