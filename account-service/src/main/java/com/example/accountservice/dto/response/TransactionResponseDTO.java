package com.example.accountservice.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransactionResponseDTO {
    private Long transactionId;
    private String accountNumber;
    private LocalDateTime date;
    private String transactionType;
    private Double amount;
    private Double balance;
}
