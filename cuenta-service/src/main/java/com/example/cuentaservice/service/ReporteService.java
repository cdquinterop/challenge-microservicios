package com.example.cuentaservice.service;

import com.example.cuentaservice.dto.response.ReporteResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ReporteService {
    List<ReporteResponseDTO> getReporte(String fechaInicio, String fechaFin, Long clienteId);
}
