package com.example.personaservice.entity;


import jakarta.persistence.*;
import lombok.Data;


@MappedSuperclass
@Data
public abstract class Persona {

    @Column(nullable = false)
    private String nombre;

    @Column
    private String genero;

    @Column
    private Integer edad;

    @Column(nullable = false, unique = true)
    private String identificacion;

    @Column
    private String direccion;

    @Column
    private String telefono;
}
