package com.example.cuentaservice.service;

import com.example.cuentaservice.dto.response.ReporteResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface ReporteService {
    List<ReporteResponseDTO> getReporte(LocalDate fechaInicio, LocalDate fechaFin, Long clienteId);
}
