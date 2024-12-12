package com.example.cuentaservice.dto.cache;

import lombok.Data;

@Data
public class ClienteCacheDTO {
    private Long clienteId;
    private String nombre;
    private Boolean estado;
}