package com.example.cuentaservice.dto.request;

import lombok.Data;

@Data
public class CuentaRequestDTO {
    private String tipoCuenta;
    private Double saldoInicial;
    private Long clienteId;
}
