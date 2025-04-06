package com.example.cuentaservice.controller;

import com.example.cuentaservice.dto.request.CuentaRequestDTO;
import com.example.cuentaservice.dto.response.CuentaResponseDTO;
import com.example.cuentaservice.service.CuentaService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/cuentas")
@RequiredArgsConstructor
public class CuentaController {

    private final CuentaService cuentaService;

    @Operation(summary = "Listar todas las cuentas")
    @GetMapping
    public ResponseEntity<List<CuentaResponseDTO>> getAllCuentas() {
        return ResponseEntity.ok(cuentaService.findAll());
    }

    @Operation(summary = "Obtener cuenta por ID")
    @GetMapping("/{id}")
    public ResponseEntity<CuentaResponseDTO> getCuentaById(@PathVariable Long id) {
        return ResponseEntity.ok(cuentaService.findById(id));
    }

    @Operation(summary = "Crear una nueva cuenta")
    @PostMapping
    public ResponseEntity<CuentaResponseDTO> createCuenta(@Valid @RequestBody CuentaRequestDTO cuentaRequestDTO) {
        CuentaResponseDTO cuenta = cuentaService.create(cuentaRequestDTO);
        return ResponseEntity
                .created(URI.create("/api/cuentas/" + cuenta.getCuentaId()))
                .body(cuenta);
    }

    @Operation(summary = "Actualizar una cuenta existente")
    @PutMapping("/{id}")
    public ResponseEntity<CuentaResponseDTO> updateCuenta(@PathVariable Long id, @Valid @RequestBody CuentaRequestDTO cuentaRequestDTO) {
        return ResponseEntity.ok(cuentaService.update(id, cuentaRequestDTO));
    }

    @Operation(summary = "Eliminar una cuenta por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCuenta(@PathVariable Long id) {
        cuentaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
