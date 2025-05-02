package com.example.accountservice.service.impl;

import com.example.accountservice.dto.request.TransactionRequestDTO;
import com.example.accountservice.dto.response.TransactionResponseDTO;
import com.example.accountservice.entity.Account;
import com.example.accountservice.entity.Transaction;
import com.example.accountservice.exception.AccountServiceException;
import com.example.accountservice.exception.TransactionServiceException;
import com.example.accountservice.repository.AccountRepository;
import com.example.accountservice.repository.TransactionRepository;
import com.example.accountservice.service.TransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.accountservice.exception.AccountServiceException.*;
import static com.example.accountservice.exception.TransactionServiceException.*;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;

    private static final String DEPOSIT = "DEPOSIT";
    private static final String WITHDRAWAL = "WITHDRAWAL";

    @Override
    public List<TransactionResponseDTO> findAll() {
        return transactionRepository.findAll().stream()
                .map(tx -> {
                    TransactionResponseDTO dto = modelMapper.map(tx, TransactionResponseDTO.class);
                    dto.setAccountNumber(tx.getAccount().getAccountNumber());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TransactionResponseDTO create(TransactionRequestDTO dto) {
        Account account = getActiveAccount(dto.getAccountId());
        validateTransaction(dto);

        double newBalance = calculateNewBalance(dto.getTransactionType(), dto.getAmount(), account.getAvailableBalance());

        Transaction transaction = Transaction.builder()
                .date(LocalDateTime.now())
                .transactionType(dto.getTransactionType().toUpperCase())
                .amount(getSignedAmount(dto.getTransactionType(), dto.getAmount()))
                .balance(newBalance)
                .account(account)
                .build();

        Transaction saved = transactionRepository.save(transaction);

        account.setAvailableBalance(newBalance);
        accountRepository.save(account);

        return modelMapper.map(saved, TransactionResponseDTO.class);
    }

    @Override
    @Transactional
    public TransactionResponseDTO update(Long id, TransactionRequestDTO dto) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionServiceException(TRANSACTION_NOT_FOUND));

        Account account = getActiveAccount(dto.getAccountId());
        validateTransaction(dto);

        double newBalance = calculateNewBalance(dto.getTransactionType(), dto.getAmount(), account.getAvailableBalance());

        transaction.setDate(LocalDateTime.now());
        transaction.setTransactionType(dto.getTransactionType().toUpperCase());
        transaction.setAmount(getSignedAmount(dto.getTransactionType(), dto.getAmount()));
        transaction.setBalance(newBalance);
        transaction.setAccount(account);

        Transaction updated = transactionRepository.save(transaction);

        account.setAvailableBalance(newBalance);
        accountRepository.save(account);

        return modelMapper.map(updated, TransactionResponseDTO.class);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionServiceException(TRANSACTION_NOT_FOUND));

        Account account = transaction.getAccount();
        double newBalance = account.getAvailableBalance() - transaction.getAmount();

        transactionRepository.delete(transaction);

        account.setAvailableBalance(newBalance);
        accountRepository.save(account);
    }

    private Account getActiveAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountServiceException(ACCOUNT_NOT_FOUND));

        if (!Boolean.TRUE.equals(account.getStatus())) {
            throw new AccountServiceException(INACTIVE_ACCOUNT);
        }
        return account;
    }

    private void validateTransaction(TransactionRequestDTO dto) {
        String type = dto.getTransactionType().toUpperCase();

        if (!List.of(DEPOSIT, WITHDRAWAL).contains(type)) {
            throw new TransactionServiceException(INVALID_TRANSACTION_TYPE);
        }

        if (dto.getAmount() == null || dto.getAmount() <= 0) {
            throw new TransactionServiceException(INVALID_TRANSACTION_AMOUNT);
        }
    }

    private double calculateNewBalance(String transactionType, Double amount, Double currentBalance) {
        String type = transactionType.toUpperCase();

        if (WITHDRAWAL.equals(type)) {
            double newBalance = currentBalance - amount;
            if (newBalance < 0) {
                throw new AccountServiceException(INSUFFICIENT_BALANCE);
            }
            return newBalance;
        }
        return currentBalance + amount;
    }

    private double getSignedAmount(String transactionType, Double amount) {
        return WITHDRAWAL.equalsIgnoreCase(transactionType) ? -amount : amount;
    }
}
