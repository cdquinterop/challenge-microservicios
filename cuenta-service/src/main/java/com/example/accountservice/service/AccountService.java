package com.example.accountservice.service;

import com.example.accountservice.dto.request.AccountRequestDTO;
import com.example.accountservice.dto.response.AccountResponseDTO;

import java.util.List;

public interface CuentaService {
    List<AccountResponseDTO> findAll();
    AccountResponseDTO findById(Long id);
    AccountResponseDTO create(AccountRequestDTO accountRequestDTO);
    AccountResponseDTO update(Long id, AccountRequestDTO accountRequestDTO);
    void delete(Long id);
}
