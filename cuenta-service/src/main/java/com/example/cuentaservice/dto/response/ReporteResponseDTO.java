package com.example.cuentaservice.dto.response;


import lombok.Data;

import java.util.List;

@Data
public class ReporteResponseDTO {
    private String numeroCuenta;
    private Double saldoInicial;
    private Double saldoDisponible;
    private List<MovimientoResponseDTO> movimientos;
}
