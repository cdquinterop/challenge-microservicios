package com.example.personaservice.service;


import com.example.personaservice.dto.request.ClienteRequestDTO;
import com.example.personaservice.dto.response.ClienteResponseDTO;
import com.example.personaservice.entity.Cliente;
import com.example.personaservice.exception.ClienteServiceException;
import com.example.personaservice.repository.ClienteRepository;
import com.example.personaservice.service.impl.ClienteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
class ClienteServiceImplTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCliente_Successful() {
        ClienteRequestDTO request = new ClienteRequestDTO();
        request.setNombre("Juan Pérez");
        request.setDireccion("Calle Falsa 123");
        request.setIdentificacion("12345");
        request.setContraseña("password");

        Cliente cliente = new Cliente();
        cliente.setClienteId(1L);
        cliente.setNombre("Juan Pérez");
        cliente.setDireccion("Calle Falsa 123");
        cliente.setEstado(true);

        when(clienteRepository.findByIdentificacion("12345")).thenReturn(Optional.empty());
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        ClienteResponseDTO response = clienteService.create(request);

        assertThat(response).isNotNull();
        assertThat(response.getNombre()).isEqualTo("Juan Pérez");
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void testCreateCliente_NullName() {

        ClienteRequestDTO request = new ClienteRequestDTO();
        request.setDireccion("Calle Falsa 123");
        request.setIdentificacion("12345");
        request.setContraseña("password");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> clienteService.create(request));
        assertThat(exception.getMessage()).isEqualTo("El nombre del cliente no puede estar vacío.");
        verifyNoInteractions(clienteRepository);
    }

    @Test
    void testCreateCliente_AlreadyRegistered() {

        ClienteRequestDTO request = new ClienteRequestDTO();
        request.setNombre("Juan Pérez");
        request.setDireccion("Calle Falsa 123");
        request.setIdentificacion("12345");
        request.setContraseña("password");

        when(clienteRepository.findByIdentificacion("12345")).thenReturn(Optional.of(new Cliente()));

        ClienteServiceException exception = assertThrows(ClienteServiceException.class, () -> clienteService.create(request));
        assertThat(exception.getMessage()).contains("ya registrado con la identificación: 12345");
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void testCreateCliente_RepositoryThrowsException() {

        ClienteRequestDTO request = new ClienteRequestDTO();
        request.setNombre("Juan Pérez");
        request.setDireccion("Calle Falsa 123");
        request.setIdentificacion("12345");
        request.setContraseña("password");

        when(clienteRepository.findByIdentificacion("12345")).thenReturn(Optional.empty());
        when(clienteRepository.save(any(Cliente.class))).thenThrow(new RuntimeException("Error al guardar en la base de datos"));


        RuntimeException exception = assertThrows(RuntimeException.class, () -> clienteService.create(request));
        assertThat(exception.getMessage()).isEqualTo("Error al guardar en la base de datos");
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }
}