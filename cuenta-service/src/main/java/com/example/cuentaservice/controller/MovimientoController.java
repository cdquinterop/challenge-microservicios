package com.example.cuentaservice.controller;

import com.example.cuentaservice.dto.request.MovimientoRequestDTO;
import com.example.cuentaservice.dto.response.MovimientoResponseDTO;
import com.example.cuentaservice.service.MovimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

    private final MovimientoService movimientoService;

    @GetMapping
    public ResponseEntity<List<MovimientoResponseDTO>> getAllMovimientos() {
        return ResponseEntity.ok(movimientoService.findAll());
    }

    @PostMapping
    public ResponseEntity<MovimientoResponseDTO> createMovimiento(@RequestBody MovimientoRequestDTO movimientoRequestDTO) {
        return ResponseEntity.ok(movimientoService.create(movimientoRequestDTO));
    }
    @PutMapping("/{id}")
    public ResponseEntity<MovimientoResponseDTO> updateMovimiento(@PathVariable Long id, @RequestBody MovimientoRequestDTO movimientoRequestDTO) {
        MovimientoResponseDTO updatedMovimiento = movimientoService.update(id, movimientoRequestDTO);
        return ResponseEntity.ok(updatedMovimiento);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovimiento(@PathVariable Long id) {
        movimientoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
