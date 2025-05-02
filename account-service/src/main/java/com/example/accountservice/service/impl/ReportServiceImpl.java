package com.example.accountservice.service.impl;

import com.example.accountservice.dto.response.TransactionResponseDTO;
import com.example.accountservice.dto.response.ReportResponseDTO;
import com.example.accountservice.entity.Account;
import com.example.accountservice.entity.Transaction;
import com.example.accountservice.exception.ReportServiceException;
import com.example.accountservice.repository.AccountRepository;
import com.example.accountservice.repository.TransactionRepository;
import com.example.accountservice.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.accountservice.exception.ReportServiceException.CUSTOMER_WITHOUT_ACCOUNTS;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<ReportResponseDTO> getReport(LocalDate startDate, LocalDate endDate, Long customerId) {
        LocalDateTime from = startDate.atStartOfDay();
        LocalDateTime to = endDate.atTime(23, 59, 59);

        List<Account> accounts = accountRepository.findAllByCustomerId(customerId);
        if (accounts.isEmpty()) {
            throw new ReportServiceException(CUSTOMER_WITHOUT_ACCOUNTS);
        }

        return accounts.stream()
                .map(account -> buildReportResponse(account, from, to))
                .collect(Collectors.toList());
    }

    private ReportResponseDTO buildReportResponse(Account account, LocalDateTime from, LocalDateTime to) {
        List<Transaction> transactions = transactionRepository.findAllByAccount_AccountIdAndDateBetween(
                account.getAccountId(), from, to);

        List<TransactionResponseDTO> transactionDTOs = transactions.stream()
                .map(tx -> {
                    TransactionResponseDTO dto = modelMapper.map(tx, TransactionResponseDTO.class);
                    dto.setAccountNumber(account.getAccountNumber());
                    return dto;
                })
                .collect(Collectors.toList());

        return ReportResponseDTO.builder()
                .accountNumber(account.getAccountNumber())
                .initialBalance(account.getInitialBalance())
                .availableBalance(account.getAvailableBalance())
                .transactions(transactionDTOs)
                .build();
    }
}
