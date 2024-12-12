package com.example.cuentaservice.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MovimientoResponseDTO {
    private Long id;
    private String numeroCuenta;
    private LocalDateTime fecha;
    private String tipoMovimiento;
    private Double valor;
    private Double saldo;

}
