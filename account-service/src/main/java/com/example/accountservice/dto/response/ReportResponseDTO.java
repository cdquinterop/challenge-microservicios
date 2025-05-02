package com.example.accountservice.dto.response;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReportResponseDTO {

    @Schema(description = "Account number associated with the report")
    private String accountNumber;

    @Schema(description = "Initial balance at the beginning of the report period")
    private Double initialBalance;

    @Schema(description = "Available balance at the end of the report period")
    private Double availableBalance;

    @Schema(description = "List of transactions during the period")
    private List<TransactionResponseDTO> transactions;
}
