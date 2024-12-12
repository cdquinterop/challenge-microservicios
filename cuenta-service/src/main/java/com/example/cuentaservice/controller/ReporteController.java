package com.example.cuentaservice.controller;

import com.example.cuentaservice.dto.response.ReporteResponseDTO;
import com.example.cuentaservice.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping
    public ResponseEntity<List<ReporteResponseDTO>> getReporte(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin,
            @RequestParam Long clienteId) {
        List<ReporteResponseDTO> reportes = reporteService.getReporte(fechaInicio, fechaFin, clienteId);
        return ResponseEntity.ok(reportes);
    }
}
