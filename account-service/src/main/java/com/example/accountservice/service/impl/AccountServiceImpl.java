package com.example.accountservice.service.impl;

import com.example.accountservice.dto.request.AccountRequestDTO;
import com.example.accountservice.dto.response.AccountResponseDTO;
import com.example.accountservice.entity.Account;
import com.example.accountservice.entity.Customer;
import com.example.accountservice.exception.AccountServiceException;
import com.example.accountservice.repository.CustomerRepository;
import com.example.accountservice.repository.AccountRepository;
import com.example.accountservice.service.AccountService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static com.example.accountservice.exception.AccountServiceException.*;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<AccountResponseDTO> findAll() {
        return accountRepository.findAll().stream()
                .map(account -> modelMapper.map(account, AccountResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public AccountResponseDTO findById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountServiceException(ACCOUNT_NOT_FOUND));
        return modelMapper.map(account, AccountResponseDTO.class);
    }

    @Override
    @Transactional
    public AccountResponseDTO create(AccountRequestDTO dto) {
        Customer customer = customerRepository.findByCustomerId(dto.getCustomerId())
                .orElseThrow(() -> new AccountServiceException(CUSTOMER_NOT_FOUND));

        if (!Boolean.TRUE.equals(customer.getStatus())) {
            throw new AccountServiceException(INACTIVE_CUSTOMER);
        }

        Account account = Account.builder()
                .accountNumber(generateAccountNumber())
                .accountType(dto.getAccountType())
                .initialBalance(dto.getInitialBalance())
                .availableBalance(dto.getInitialBalance())
                .status(true)
                .customerId(dto.getCustomerId())
                .build();

        Account saved = accountRepository.save(account);
        return modelMapper.map(saved, AccountResponseDTO.class);
    }

    @Override
    @Transactional
    public AccountResponseDTO update(Long id, AccountRequestDTO dto) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountServiceException(ACCOUNT_NOT_FOUND));

        account.setAccountType(dto.getAccountType());
        account.setInitialBalance(dto.getInitialBalance());

        Account updated = accountRepository.save(account);
        return modelMapper.map(updated, AccountResponseDTO.class);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new AccountServiceException(ACCOUNT_NOT_FOUND);
        }
        accountRepository.deleteById(id);
    }

    private String generateAccountNumber() {
        return String.format("%06d", 100000 + new Random().nextInt(900000));
    }
}
