package com.example.accountservice.repository;

import com.example.accountservice.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByCuenta_CuentaIdAndFechaBetween(Long cuentaId, LocalDateTime inicio, LocalDateTime fin);
}
