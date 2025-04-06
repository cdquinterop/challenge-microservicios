package com.example.personaservice.dto.request;

import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClienteRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no debe superar los 100 caracteres")
    private String nombre;

    @Size(max = 20, message = "El género no debe superar los 20 caracteres")
    private String genero;

    @Min(value = 0, message = "La edad no puede ser negativa")
    private Integer edad;

    @NotBlank(message = "La identificación es obligatoria")
    private String identificacion;

    @Size(max = 150, message = "La dirección no debe superar los 150 caracteres")
    private String direccion;

    @Size(max = 20, message = "El teléfono no debe superar los 20 caracteres")
    private String telefono;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres")
    private String contraseña;

    @NotNull(message = "El estado es obligatorio")
    private Boolean estado;
}
