package com.example.accountservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequestDTO {

    @NotBlank(message = "Account type is required")
    private String accountType;

    @NotNull(message = "Initial balance cannot be null")
    @PositiveOrZero(message = "Initial balance cannot be negative")
    private Double initialBalance;

    @NotNull(message = "Customer ID is required")
    private Long customerId;
}
