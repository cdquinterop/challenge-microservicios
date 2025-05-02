package com.example.accountservice.service;

import com.example.accountservice.dto.response.ReportResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface ReporteService {
    List<ReportResponseDTO> getReporte(LocalDate fechaInicio, LocalDate fechaFin, Long clienteId);
}
