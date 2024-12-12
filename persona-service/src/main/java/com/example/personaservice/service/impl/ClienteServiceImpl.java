package com.example.personaservice.service.impl;

import com.example.personaservice.config.RabbitMQConfig;
import com.example.personaservice.dto.event.ClienteEventDTO;
import com.example.personaservice.dto.request.ClienteRequestDTO;
import com.example.personaservice.dto.response.ClienteResponseDTO;
import com.example.personaservice.entity.Cliente;
import com.example.personaservice.exception.ClienteServiceException;
import com.example.personaservice.repository.ClienteRepository;
import com.example.personaservice.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ModelMapper modelMapper = new ModelMapper();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public List<ClienteResponseDTO> findAll() {
        return clienteRepository.findAll().stream()
                .map(cliente -> modelMapper.map(cliente, ClienteResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public ClienteResponseDTO findById(Long id) {
        Cliente cliente = findClienteOrThrow(id);
        return modelMapper.map(cliente, ClienteResponseDTO.class);
    }

    @Override
    public ClienteResponseDTO create(ClienteRequestDTO clienteRequestDTO) {
        // Validación del nombre
        if (clienteRequestDTO.getNombre() == null || clienteRequestDTO.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException(ClienteServiceException.CLIENTE_NAME_EMPTY);
        }

        // Validación de identificación duplicada
        if (clienteRepository.findByIdentificacion(clienteRequestDTO.getIdentificacion()).isPresent()) {
            throw new ClienteServiceException(ClienteServiceException.CLIENTE_ALREADY_REGISTERED + clienteRequestDTO.getIdentificacion());
        }

        Cliente cliente = modelMapper.map(clienteRequestDTO, Cliente.class);
        cliente.setContraseña(passwordEncoder.encode(clienteRequestDTO.getContraseña()));

        try {
            Cliente savedCliente = clienteRepository.save(cliente);
            publishClienteEvent("CREATE", savedCliente);
            return modelMapper.map(savedCliente, ClienteResponseDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar en la base de datos", e);
        }
    }

    @Override
    public ClienteResponseDTO update(Long id, ClienteRequestDTO clienteRequestDTO) {
        Cliente existingCliente = findClienteOrThrow(id);

        Cliente updatedCliente = modelMapper.map(clienteRequestDTO, Cliente.class);
        updatedCliente.setClienteId(existingCliente.getClienteId());
        updatedCliente.setContraseña(getUpdatedPassword(clienteRequestDTO.getContraseña(), existingCliente.getContraseña()));

        try {
            Cliente savedCliente = clienteRepository.save(updatedCliente);
            publishClienteEvent("UPDATE", savedCliente);
            return modelMapper.map(savedCliente, ClienteResponseDTO.class);
        } catch (Exception e) {
            throw new ClienteServiceException(ClienteServiceException.CLIENTE_UPDATE_ERROR, e);
        }
    }

    @Override
    public void delete(Long id) {
        Cliente cliente = findClienteOrThrow(id);

        try {
            clienteRepository.delete(cliente);
            cliente.setEstado(false);
            publishClienteEvent("DELETE", cliente);
        } catch (Exception e) {
            throw new ClienteServiceException(ClienteServiceException.CLIENTE_DELETION_ERROR, e);
        }
    }

    private Cliente findClienteOrThrow(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteServiceException(ClienteServiceException.CLIENTE_NOT_FOUND + " con ID: " + id));
    }

    private String getUpdatedPassword(String newPassword, String existingPassword) {
        if (newPassword != null && !newPassword.isEmpty()) {
            return passwordEncoder.encode(newPassword);
        }
        return existingPassword;
    }

    private void publishClienteEvent(String action, Cliente cliente) {
        try {
            ClienteEventDTO clienteEvent = new ClienteEventDTO(
                    action,
                    cliente.getClienteId(),
                    cliente.getNombre(),
                    cliente.getEstado()
            );

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.CLIENTE_EXCHANGE,
                    RabbitMQConfig.CLIENTE_ROUTING_KEY,
                    clienteEvent
            );

            System.out.println("Evento publicado: " + clienteEvent);
        } catch (Exception e) {
            throw new ClienteServiceException("Error al publicar evento RabbitMQ", e);
        }
    }
}


