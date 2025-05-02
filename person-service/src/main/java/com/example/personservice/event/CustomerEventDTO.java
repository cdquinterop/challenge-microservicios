package com.example.personservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEventDTO {
    private String action;
    private Long clienteId;
    private String nombre;
    private Boolean estado;
}
