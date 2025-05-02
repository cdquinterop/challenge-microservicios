package com.example.accountservice.controller;

import com.example.accountservice.dto.request.MovimientoRequestDTO;
import com.example.accountservice.dto.response.MovimientoResponseDTO;
import com.example.accountservice.service.MovimientoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

    private final MovimientoService movimientoService;

    @Operation(summary = "Listar todos los movimientos")
    @GetMapping
    public ResponseEntity<List<MovimientoResponseDTO>> getAllMovimientos() {
        return ResponseEntity.ok(movimientoService.findAll());
    }

    @Operation(summary = "Crear un nuevo movimiento")
    @PostMapping
    public ResponseEntity<MovimientoResponseDTO> createMovimiento(@Valid @RequestBody MovimientoRequestDTO movimientoRequestDTO) {
        MovimientoResponseDTO movimiento = movimientoService.create(movimientoRequestDTO);
        return ResponseEntity
                .created(URI.create("/api/movimientos/" + movimiento.getMovimientoId()))
                .body(movimiento);
    }

    @Operation(summary = "Actualizar un movimiento")
    @PutMapping("/{id}")
    public ResponseEntity<MovimientoResponseDTO> updateMovimiento(@PathVariable Long id, @Valid @RequestBody MovimientoRequestDTO movimientoRequestDTO) {
        MovimientoResponseDTO updatedMovimiento = movimientoService.update(id, movimientoRequestDTO);
        return ResponseEntity.ok(updatedMovimiento);
    }

    @Operation(summary = "Eliminar un movimiento")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovimiento(@PathVariable Long id) {
        movimientoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
