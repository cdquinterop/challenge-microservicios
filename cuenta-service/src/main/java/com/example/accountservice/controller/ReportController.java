package com.example.accountservice.controller;


import com.example.accountservice.dto.response.ReporteResponseDTO;
import com.example.accountservice.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @Operation(summary = "Generar reporte de movimientos por cliente y rango de fechas")
    @GetMapping
    public ResponseEntity<List<ReporteResponseDTO>> getReporte(
            @Parameter(description = "Fecha de inicio en formato yyyy-MM-dd")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,

            @Parameter(description = "Fecha de fin en formato yyyy-MM-dd")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,

            @Parameter(description = "ID del cliente")
            @RequestParam Long clienteId
    ) {
        List<ReporteResponseDTO> reportes = reporteService.getReporte(fechaInicio, fechaFin, clienteId);
        return ResponseEntity.ok(reportes);
    }
}
