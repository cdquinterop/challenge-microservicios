package com.example.personaservice.controller;

import com.example.personaservice.dto.request.ClienteRequestDTO;
import com.example.personaservice.dto.response.ClienteResponseDTO;
import com.example.personaservice.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {
    private final ClienteService clienteService;

    @GetMapping
    public List<ClienteResponseDTO> getAllClientes() {
        return clienteService.findAll();
    }

    @GetMapping("/{id}")
    public ClienteResponseDTO getClienteById(@PathVariable Long id) {
        return clienteService.findById(id);
    }

    @PostMapping
    public ClienteResponseDTO createCliente( @RequestBody ClienteRequestDTO clienteRequestDTO) {
        return clienteService.create(clienteRequestDTO);
    }

    @PutMapping("/{id}")
    public ClienteResponseDTO updateCliente(@PathVariable Long id,  @RequestBody ClienteRequestDTO clienteRequestDTO) {
        return clienteService.update(id, clienteRequestDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        clienteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
