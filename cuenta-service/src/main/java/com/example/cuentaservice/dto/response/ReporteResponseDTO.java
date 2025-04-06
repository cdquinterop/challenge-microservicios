package com.example.cuentaservice.dto.response;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReporteResponseDTO {

    @Schema(description = "Número de cuenta asociada al reporte")
    private String numeroCuenta;

    @Schema(description = "Saldo inicial de la cuenta en la fecha de inicio del reporte")
    private Double saldoInicial;

    @Schema(description = "Saldo disponible al final del periodo")
    private Double saldoDisponible;

    @Schema(description = "Lista de movimientos realizados durante el período")
    private List<MovimientoResponseDTO> movimientos;
}
