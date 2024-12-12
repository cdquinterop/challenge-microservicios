package com.example.cuentaservice.dto.response;

import lombok.Data;

@Data
public class CuentaResponseDTO {
    private Long id;
    private String numeroCuenta;
    private String tipoCuenta;
    private Double saldoInicial;
    private Boolean estado;
}
