package com.example.personservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CustomerRequestDTO {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Size(max = 20, message = "Gender must not exceed 20 characters")
    private String gender;

    @Min(value = 0, message = "Age cannot be negative")
    private Integer age;

    @NotBlank(message = "Identification is required")
    private String identification;

    @Size(max = 150, message = "Address must not exceed 150 characters")
    private String address;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 4, message = "Password must be at least 4 characters long")
    private String password;

    @NotNull(message = "Status is required")
    private Boolean status;
}
