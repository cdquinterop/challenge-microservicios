package com.example.personservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.*;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public abstract class Person {

    @Column(nullable = false)
    private String name;

    @Column
    private String gender;

    @Column
    private Integer age;

    @Column(nullable = false, unique = true)
    private String identification;

    @Column
    private String address;

    @Column
    private String phone;
}
