package com.example.accountservice.controller;

import com.example.accountservice.dto.request.TransactionRequestDTO;
import com.example.accountservice.dto.response.TransactionResponseDTO;
import com.example.accountservice.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(summary = "List all transactions")
    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.findAll());
    }

    @Operation(summary = "Create a new transaction")
    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(@Valid @RequestBody TransactionRequestDTO requestDTO) {
        TransactionResponseDTO transaction = transactionService.create(requestDTO);
        return ResponseEntity
                .created(URI.create("/api/transactions/" + transaction.getTransactionId()))
                .body(transaction);
    }

    @Operation(summary = "Update an existing transaction")
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> updateTransaction(@PathVariable Long id, @Valid @RequestBody TransactionRequestDTO requestDTO) {
        TransactionResponseDTO updated = transactionService.update(id, requestDTO);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete a transaction")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
