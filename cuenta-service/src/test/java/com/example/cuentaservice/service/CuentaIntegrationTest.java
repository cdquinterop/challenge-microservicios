package com.example.cuentaservice.service;

import com.example.cuentaservice.CuentaServiceApplication;
import com.example.cuentaservice.cache.ClienteCache;
import com.example.cuentaservice.dto.cache.ClienteCacheDTO;
import com.example.cuentaservice.dto.event.ClienteEventDTO;
import com.example.cuentaservice.dto.request.CuentaRequestDTO;
import com.example.cuentaservice.dto.response.CuentaResponseDTO;
import com.example.cuentaservice.exception.CuentaServiceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CuentaServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CuentaIntegrationTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Queue clienteQueue;

    @Autowired
    private ClienteCache clienteCache;

    @Autowired
    private CuentaService cuentaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreacionClienteYCuenta() throws Exception {
        // Enviar evento de creación de cliente a la cola
        long clienteId = System.currentTimeMillis() + (long) (Math.random() * 1000);
        ClienteEventDTO clienteEvent = new ClienteEventDTO();
        clienteEvent.setAction("CREATE");
        clienteEvent.setClienteId(clienteId);
        clienteEvent.setNombre("Juan Perez");
        clienteEvent.setEstado(true);

        rabbitTemplate.convertAndSend(clienteQueue.getName(), clienteEvent);

        Thread.sleep(2000);

        ClienteCacheDTO clienteCacheDTO = clienteCache.getCliente(clienteId);
        Assertions.assertNotNull(clienteCacheDTO, "El cliente debería existir en cache después del evento CREATE");
        Assertions.assertEquals("Juan Perez", clienteCacheDTO.getNombre());
        Assertions.assertTrue(clienteCacheDTO.getEstado());

        CuentaRequestDTO requestDTO = new CuentaRequestDTO();
        requestDTO.setClienteId(clienteId);
        requestDTO.setSaldoInicial(1000.0);
        requestDTO.setTipoCuenta("AHORRO");

        CuentaResponseDTO responseDTO = cuentaService.create(requestDTO);

        Assertions.assertNotNull(responseDTO, "La respuesta de creación de cuenta no debería ser null");
        Assertions.assertNotNull(responseDTO.getNumeroCuenta(), "La cuenta debería tener un número de cuenta generado");
        Assertions.assertTrue(responseDTO.getEstado(), "La cuenta debería crearse activa");
        Assertions.assertEquals(1000.0, responseDTO.getSaldoInicial());
    }

    @Test
    public void testClienteInactivoNoPermiteCreacionDeCuenta() throws Exception {
        // Crear cliente
        long clienteId = System.currentTimeMillis() + (long) (Math.random() * 1000);
        ClienteEventDTO clienteEvent = new ClienteEventDTO();
        clienteEvent.setAction("CREATE");
        clienteEvent.setClienteId(clienteId);
        clienteEvent.setNombre("Cliente Inactivo");
        clienteEvent.setEstado(true);

        rabbitTemplate.convertAndSend(clienteQueue.getName(), clienteEvent);
        Thread.sleep(2000);

        ClienteEventDTO clienteEventInactivo = new ClienteEventDTO();
        clienteEventInactivo.setAction("DELETE");
        clienteEventInactivo.setClienteId(clienteId);

        rabbitTemplate.convertAndSend(clienteQueue.getName(), clienteEventInactivo);
        Thread.sleep(2000);

        ClienteCacheDTO clienteCacheDTO = clienteCache.getCliente(clienteId);
        Assertions.assertNotNull(clienteCacheDTO);
        Assertions.assertFalse(clienteCacheDTO.getEstado(), "Cliente inactivo");

        CuentaRequestDTO requestDTO = new CuentaRequestDTO();
        requestDTO.setClienteId(Long.valueOf(clienteId));
        requestDTO.setSaldoInicial(500.0);

        Assertions.assertThrows(CuentaServiceException.class, () -> {
            cuentaService.create(requestDTO);
        }, "Error cliente inactivo");
    }
}
