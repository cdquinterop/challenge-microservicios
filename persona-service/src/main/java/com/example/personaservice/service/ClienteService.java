package com.example.personaservice.service;

import com.example.personaservice.dto.request.ClienteRequestDTO;
import com.example.personaservice.dto.response.ClienteResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface ClienteService {
    List<ClienteResponseDTO> findAll();
    ClienteResponseDTO findById(Long id);
    ClienteResponseDTO create(ClienteRequestDTO clienteRequestDTO);
    ClienteResponseDTO update(Long id, ClienteRequestDTO clienteRequestDTO);
    void delete(Long id);
}