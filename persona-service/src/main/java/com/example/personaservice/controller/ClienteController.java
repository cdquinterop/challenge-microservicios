package com.example.personaservice.controller;

import com.example.personaservice.dto.request.ClienteRequestDTO;
import com.example.personaservice.dto.response.ClienteResponseDTO;
import com.example.personaservice.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Slf4j
public class ClienteController {

    private final ClienteService clienteService;

    @Operation(summary = "Obtener todos los clientes")
    @ApiResponse(responseCode = "200", description = "Listado exitoso")
    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> getAllClientes() {
        log.info("Obteniendo todos los clientes");
        return ResponseEntity.ok(clienteService.findAll());
    }

    @Operation(summary = "Obtener un cliente por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> getClienteById(@PathVariable Long id) {
        log.info("Buscando cliente con ID {}", id);
        return ResponseEntity.ok(clienteService.findById(id));
    }

    @Operation(summary = "Crear un nuevo cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> createCliente(@Valid @RequestBody ClienteRequestDTO clienteRequestDTO) {
        log.info("Creando cliente: {}", clienteRequestDTO);
        ClienteResponseDTO cliente = clienteService.create(clienteRequestDTO);
        return ResponseEntity
                .created(URI.create("/api/clientes/" + cliente.getClienteId()))
                .body(cliente);
    }

    @Operation(summary = "Actualizar un cliente existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> updateCliente(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequestDTO clienteRequestDTO) {
        log.info("Actualizando cliente con ID {}: {}", id, clienteRequestDTO);
        return ResponseEntity.ok(clienteService.update(id, clienteRequestDTO));
    }

    @Operation(summary = "Eliminar un cliente por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cliente eliminado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        log.info("Eliminando cliente con ID {}", id);
        clienteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
