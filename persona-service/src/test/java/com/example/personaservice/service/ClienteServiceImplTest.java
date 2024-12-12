package com.example.personaservice.service;


import com.example.personaservice.dto.request.ClienteRequestDTO;
import com.example.personaservice.dto.response.ClienteResponseDTO;
import com.example.personaservice.entity.Cliente;
import com.example.personaservice.repository.ClienteRepository;
import com.example.personaservice.service.impl.ClienteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ClienteServiceImplTest {

    @Mock
    private ClienteRepository clienteRepository;

    private final ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private ClienteServiceImpl clienteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCliente_Successful() {
        // Arrange
        ClienteRequestDTO request = new ClienteRequestDTO();
        request.setNombre("Juan Pérez");
        request.setDireccion("Calle Falsa 123");

        Cliente cliente = new Cliente();
        cliente.setClienteId(1L);
        cliente.setNombre("Juan Pérez");
        cliente.setDireccion("Calle Falsa 123");

        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        // Act
        ClienteResponseDTO response = clienteService.create(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getNombre()).isEqualTo("Juan Pérez");
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void testCreateCliente_NullName() {
        // Arrange
        ClienteRequestDTO request = new ClienteRequestDTO();
        request.setDireccion("Calle Falsa 123");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> clienteService.create(request));
        assertThat(exception.getMessage()).isEqualTo("El nombre del cliente no puede estar vacío.");
        verifyNoInteractions(clienteRepository);
    }

    @Test
    void testCreateCliente_RepositoryThrowsException() {
        // Arrange
        ClienteRequestDTO request = new ClienteRequestDTO();
        request.setNombre("Juan Pérez");
        request.setDireccion("Calle Falsa 123");

        when(clienteRepository.save(any(Cliente.class))).thenThrow(new RuntimeException("Error al guardar en la base de datos"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> clienteService.create(request));
        assertThat(exception.getMessage()).isEqualTo("Error al guardar en la base de datos");
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }
}