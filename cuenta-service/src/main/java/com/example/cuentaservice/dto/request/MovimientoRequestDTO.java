package com.example.cuentaservice.dto.request;

import lombok.Data;

@Data
public class MovimientoRequestDTO {
    private Long cuentaId;
    private String tipoMovimiento;
    private Double monto;
}

