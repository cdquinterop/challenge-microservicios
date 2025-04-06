package com.example.cuentaservice.service;

import com.example.cuentaservice.CuentaServiceApplication;
import com.example.cuentaservice.dto.event.ClienteEventDTO;
import com.example.cuentaservice.dto.request.CuentaRequestDTO;
import com.example.cuentaservice.dto.response.CuentaResponseDTO;
import com.example.cuentaservice.entity.Cliente;
import com.example.cuentaservice.exception.CuentaServiceException;
import com.example.cuentaservice.repository.ClienteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.awaitility.Awaitility.await;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CuentaServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CuentaIntegrationTest {

    @Autowired private RabbitTemplate rabbitTemplate;
    @Autowired private Queue clienteQueue;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private CuentaService cuentaService;
    @Autowired private ObjectMapper objectMapper;

    @Test
    public void testCreacionClienteYCuenta() {
        long clienteId = generarClienteIdUnico();
        ClienteEventDTO clienteEvent = new ClienteEventDTO();
        clienteEvent.setAction("CREATE");
        clienteEvent.setClienteId(clienteId);
        clienteEvent.setNombre("Juan Perez");
        clienteEvent.setEstado(true);

        rabbitTemplate.convertAndSend(clienteQueue.getName(), clienteEvent);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            Cliente cliente = clienteRepository.findByClienteId(clienteId)
                    .orElseThrow(() -> new AssertionError("El cliente no fue persistido correctamente"));
            assertEquals("Juan Perez", cliente.getNombre());
            assertTrue(cliente.getEstado());
        });

        CuentaRequestDTO requestDTO = new CuentaRequestDTO();
        requestDTO.setClienteId(clienteId);
        requestDTO.setSaldoInicial(1000.0);
        requestDTO.setTipoCuenta("AHORRO");

        CuentaResponseDTO responseDTO = cuentaService.create(requestDTO);

        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getNumeroCuenta());
        assertTrue(responseDTO.getEstado());
        assertEquals(1000.0, responseDTO.getSaldoInicial());
    }

    @Test
    public void testClienteInactivoNoPermiteCreacionDeCuenta() {
        long clienteId = generarClienteIdUnico();

        ClienteEventDTO clienteActivo = new ClienteEventDTO();
        clienteActivo.setAction("CREATE");
        clienteActivo.setClienteId(clienteId);
        clienteActivo.setNombre("Cliente Inactivo");
        clienteActivo.setEstado(true);

        rabbitTemplate.convertAndSend(clienteQueue.getName(), clienteActivo);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            Cliente cliente = clienteRepository.findByClienteId(clienteId)
                    .orElseThrow(() -> new AssertionError("Cliente no creado aún"));
            assertTrue(cliente.getEstado());
        });

        ClienteEventDTO clienteInactivo = new ClienteEventDTO();
        clienteInactivo.setAction("DELETE");
        clienteInactivo.setClienteId(clienteId);

        rabbitTemplate.convertAndSend(clienteQueue.getName(), clienteInactivo);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            Cliente cliente = clienteRepository.findByClienteId(clienteId)
                    .orElseThrow(() -> new AssertionError("Cliente no encontrado después del evento DELETE"));
            assertFalse(cliente.getEstado(), "El cliente debería estar inactivo");
        });

        CuentaRequestDTO requestDTO = new CuentaRequestDTO();
        requestDTO.setClienteId(clienteId);
        requestDTO.setSaldoInicial(500.0);

        CuentaServiceException exception = assertThrows(CuentaServiceException.class, () -> {
            cuentaService.create(requestDTO);
        });

        assertEquals(CuentaServiceException.INACTIVE_CUSTOMER, exception.getMessage());
    }

    private long generarClienteIdUnico() {
        return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }
}
