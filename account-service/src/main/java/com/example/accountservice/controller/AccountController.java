package com.example.accountservice.controller;

import com.example.accountservice.dto.request.AccountRequestDTO;
import com.example.accountservice.dto.response.AccountResponseDTO;
import com.example.accountservice.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "List all accounts")
    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> getAllAccounts() {
        return ResponseEntity.ok(accountService.findAll());
    }

    @Operation(summary = "Get account by ID")
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> getAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.findById(id));
    }

    @Operation(summary = "Create a new account")
    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(@Valid @RequestBody AccountRequestDTO accountRequestDTO) {
        AccountResponseDTO account = accountService.create(accountRequestDTO);
        return ResponseEntity
                .created(URI.create("/api/accounts/" + account.getAccountId()))
                .body(account);
    }

    @Operation(summary = "Update an existing account")
    @PutMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> updateAccount(@PathVariable Long id, @Valid @RequestBody AccountRequestDTO accountRequestDTO) {
        return ResponseEntity.ok(accountService.update(id, accountRequestDTO));
    }

    @Operation(summary = "Delete an account by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
