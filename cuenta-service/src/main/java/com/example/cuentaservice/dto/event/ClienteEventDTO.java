package com.example.cuentaservice.dto.event;

import lombok.Data;

@Data
public class ClienteEventDTO {
    private Long clienteId;
    private String nombre;
    private Boolean estado;
    private String action;
}
