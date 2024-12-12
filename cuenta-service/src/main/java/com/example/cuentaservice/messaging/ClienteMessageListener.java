package com.example.cuentaservice.messaging;

import com.example.cuentaservice.cache.ClienteCache;
import com.example.cuentaservice.dto.cache.ClienteCacheDTO;
import com.example.cuentaservice.dto.event.ClienteEventDTO;
import com.example.cuentaservice.entity.Cuenta;
import com.example.cuentaservice.repository.CuentaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ClienteMessageListener {

    private final ClienteCache clienteCache;
    private final CuentaRepository cuentaRepository;

    @RabbitListener(queues = "clienteQueue")
    public void handleClienteEvent(ClienteEventDTO clienteEvent) {
        System.out.println("Mensaje recibido: " + clienteEvent);

        switch (clienteEvent.getAction().toUpperCase()) {
            case "CREATE":
                handleClienteCreado(clienteEvent);
                break;

            case "UPDATE":
                handleClienteActualizado(clienteEvent);
                break;

            case "DELETE":
                handleClienteEliminado(clienteEvent);
                break;

            default:
                System.out.println("Acción no reconocida: " + clienteEvent.getAction());
        }
    }

    @Transactional
    private void handleClienteCreado(ClienteEventDTO clienteEvent) {
        System.out.println("Cliente creado: " + clienteEvent);

        // Registrar en cache
        ClienteCacheDTO cliente = new ClienteCacheDTO();
        cliente.setClienteId(clienteEvent.getClienteId());
        cliente.setNombre(clienteEvent.getNombre());
        cliente.setEstado(clienteEvent.getEstado());
        clienteCache.putCliente(clienteEvent.getClienteId(), cliente);
    }

    @Transactional
    private void handleClienteActualizado(ClienteEventDTO clienteEvent) {
        System.out.println("Cliente actualizado: " + clienteEvent);

        ClienteCacheDTO cliente = clienteCache.getCliente(clienteEvent.getClienteId());
        if (cliente != null) {
            cliente.setNombre(clienteEvent.getNombre());
            cliente.setEstado(clienteEvent.getEstado());
            clienteCache.putCliente(clienteEvent.getClienteId(), cliente);
        } else {
            ClienteCacheDTO nuevoCliente = new ClienteCacheDTO();
            nuevoCliente.setClienteId(clienteEvent.getClienteId());
            nuevoCliente.setNombre(clienteEvent.getNombre());
            nuevoCliente.setEstado(clienteEvent.getEstado());
            clienteCache.putCliente(clienteEvent.getClienteId(), nuevoCliente);
        }
    }

    @Transactional
    private void handleClienteEliminado(ClienteEventDTO clienteEvent) {
        System.out.println("Cliente eliminado: " + clienteEvent);

        ClienteCacheDTO cliente = clienteCache.getCliente(clienteEvent.getClienteId());
        if (cliente != null) {
            cliente.setEstado(false);
            clienteCache.putCliente(clienteEvent.getClienteId(), cliente);
        }

        List<Cuenta> cuentas = cuentaRepository.findAllByClienteId(clienteEvent.getClienteId());
        for (Cuenta cuenta : cuentas) {
            cuenta.setEstado(false);
            cuentaRepository.save(cuenta);
        }
    }
}
