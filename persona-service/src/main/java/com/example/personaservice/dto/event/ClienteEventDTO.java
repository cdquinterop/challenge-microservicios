package com.example.personaservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteEventDTO {
    private String action;
    private Long clienteId;
    private String nombre;
    private Boolean estado;
}
