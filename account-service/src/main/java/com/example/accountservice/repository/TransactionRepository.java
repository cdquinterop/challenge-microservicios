package com.example.accountservice.repository;

import com.example.accountservice.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByAccount_AccountIdAndDateBetween(Long accountId, LocalDateTime start, LocalDateTime end);
}
