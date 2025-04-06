package com.example.personaservice.service.impl;

import com.example.personaservice.dto.request.ClienteRequestDTO;
import com.example.personaservice.dto.response.ClienteResponseDTO;
import com.example.personaservice.entity.Cliente;
import com.example.personaservice.exception.ClienteServiceException;
import com.example.personaservice.messaging.ClienteEventPublisher;
import com.example.personaservice.repository.ClienteRepository;
import com.example.personaservice.service.ClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ClienteEventPublisher clienteEventPublisher;

    @Override
    public List<ClienteResponseDTO> findAll() {
        return clienteRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ClienteResponseDTO findById(Long id) {
        return toResponse(findClienteOrThrow(id));
    }

    @Override
    public ClienteResponseDTO create(ClienteRequestDTO dto) {
        validarDatos(dto);

        if (clienteRepository.findByIdentificacion(dto.getIdentificacion()).isPresent()) {
            throw new ClienteServiceException(ClienteServiceException.CLIENTE_ALREADY_REGISTERED + dto.getIdentificacion());
        }

        Cliente cliente = toEntity(dto);
        cliente.setContraseña(passwordEncoder.encode(dto.getContraseña()));

        Cliente saved = guardarCliente(cliente, "CREATE");
        return toResponse(saved);
    }

    @Override
    public ClienteResponseDTO update(Long id, ClienteRequestDTO dto) {
        Cliente existente = findClienteOrThrow(id);

        Cliente actualizado = toEntity(dto);
        actualizado.setClienteId(existente.getClienteId());
        actualizado.setContraseña(getUpdatedPassword(dto.getContraseña(), existente.getContraseña()));

        Cliente saved = guardarCliente(actualizado, "UPDATE");
        return toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        Cliente cliente = findClienteOrThrow(id);
        cliente.setEstado(false);
        guardarCliente(cliente, "DELETE");
    }


    private Cliente findClienteOrThrow(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteServiceException(ClienteServiceException.CLIENTE_NOT_FOUND + " con ID: " + id));
    }

    private Cliente guardarCliente(Cliente cliente, String action) {
        try {
            Cliente saved = clienteRepository.save(cliente);
            clienteEventPublisher.publicarEvento(action, saved);
            return saved;
        } catch (Exception ex) {
            log.error("Error al guardar cliente. Acción: {}, ID: {}, Error: {}", action, cliente.getClienteId(), ex.getMessage(), ex);
            throw new ClienteServiceException(errorMensajePorAccion(action), ex);
        }
    }

    private String getUpdatedPassword(String nueva, String actual) {
        return (nueva != null && !nueva.isBlank()) ? passwordEncoder.encode(nueva) : actual;
    }

    private void validarDatos(ClienteRequestDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new IllegalArgumentException(ClienteServiceException.CLIENTE_NAME_EMPTY);
        }
        if (dto.getContraseña() == null || dto.getContraseña().isBlank()) {
            throw new IllegalArgumentException(ClienteServiceException.CLIENTE_PASSWORD_REQUIRED);

        }
    }

    private Cliente toEntity(ClienteRequestDTO dto) {
        return modelMapper.map(dto, Cliente.class);
    }

    private ClienteResponseDTO toResponse(Cliente cliente) {
        return modelMapper.map(cliente, ClienteResponseDTO.class);
    }

    private String errorMensajePorAccion(String action) {
        return switch (action) {
            case "CREATE" -> ClienteServiceException.CLIENTE_CREATION_ERROR;
            case "UPDATE" -> ClienteServiceException.CLIENTE_UPDATE_ERROR;
            case "DELETE" -> ClienteServiceException.CLIENTE_DELETION_ERROR;
            default -> "Error desconocido en operación de cliente.";
        };
    }
}
