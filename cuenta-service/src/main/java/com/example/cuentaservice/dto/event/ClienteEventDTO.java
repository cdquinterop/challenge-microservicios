package com.example.cuentaservice.dto.event;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ClienteEventDTO {
    private Long clienteId;
    private String nombre;
    private Boolean estado;
    private String action;
}
