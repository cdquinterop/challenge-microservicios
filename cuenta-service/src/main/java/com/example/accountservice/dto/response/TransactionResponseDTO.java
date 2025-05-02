package com.example.accountservice.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MovimientoResponseDTO {
    private Long movimientoId;
    private String numeroCuenta;
    private LocalDateTime fecha;
    private String tipoMovimiento;
    private Double valor;
    private Double saldo;
}
