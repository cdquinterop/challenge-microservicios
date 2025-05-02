package com.example.accountservice.service;

import com.example.accountservice.dto.request.TransactionRequestDTO;
import com.example.accountservice.dto.response.TransactionResponseDTO;

import java.util.List;

public interface TransactionService {
    List<TransactionResponseDTO> findAll();
    TransactionResponseDTO create(TransactionRequestDTO transactionRequestDTO);
    TransactionResponseDTO update(Long id, TransactionRequestDTO transactionRequestDTO);
    void delete(Long id);
}
