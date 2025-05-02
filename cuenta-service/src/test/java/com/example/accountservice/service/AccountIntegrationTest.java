package com.example.accountservice.service;

import com.example.accountservice.CuentaServiceApplication;
import com.example.accountservice.dto.event.CustomerEventDTO;
import com.example.accountservice.dto.request.AccountRequestDTO;
import com.example.accountservice.dto.response.AccountResponseDTO;
import com.example.accountservice.entity.Customer;
import com.example.accountservice.exception.CuentaServiceException;
import com.example.accountservice.repository.ClienteRepository;
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
        CustomerEventDTO clienteEvent = new CustomerEventDTO();
        clienteEvent.setAction("CREATE");
        clienteEvent.setClienteId(clienteId);
        clienteEvent.setNombre("Juan Perez");
        clienteEvent.setEstado(true);

        rabbitTemplate.convertAndSend(clienteQueue.getName(), clienteEvent);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            Customer customer = clienteRepository.findByClienteId(clienteId)
                    .orElseThrow(() -> new AssertionError("El cliente no fue persistido correctamente"));
            assertEquals("Juan Perez", customer.getNombre());
            assertTrue(customer.getEstado());
        });

        AccountRequestDTO requestDTO = new AccountRequestDTO();
        requestDTO.setClienteId(clienteId);
        requestDTO.setSaldoInicial(1000.0);
        requestDTO.setTipoCuenta("AHORRO");

        AccountResponseDTO responseDTO = cuentaService.create(requestDTO);

        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getNumeroCuenta());
        assertTrue(responseDTO.getEstado());
        assertEquals(1000.0, responseDTO.getSaldoInicial());
    }

    @Test
    public void testClienteInactivoNoPermiteCreacionDeCuenta() {
        long clienteId = generarClienteIdUnico();

        CustomerEventDTO clienteActivo = new CustomerEventDTO();
        clienteActivo.setAction("CREATE");
        clienteActivo.setClienteId(clienteId);
        clienteActivo.setNombre("Cliente Inactivo");
        clienteActivo.setEstado(true);

        rabbitTemplate.convertAndSend(clienteQueue.getName(), clienteActivo);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            Customer customer = clienteRepository.findByClienteId(clienteId)
                    .orElseThrow(() -> new AssertionError("Cliente no creado aún"));
            assertTrue(customer.getEstado());
        });

        CustomerEventDTO clienteInactivo = new CustomerEventDTO();
        clienteInactivo.setAction("DELETE");
        clienteInactivo.setClienteId(clienteId);

        rabbitTemplate.convertAndSend(clienteQueue.getName(), clienteInactivo);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            Customer customer = clienteRepository.findByClienteId(clienteId)
                    .orElseThrow(() -> new AssertionError("Cliente no encontrado después del evento DELETE"));
            assertFalse(customer.getEstado(), "El cliente debería estar inactivo");
        });

        AccountRequestDTO requestDTO = new AccountRequestDTO();
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
