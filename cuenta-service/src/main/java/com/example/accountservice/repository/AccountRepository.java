package com.example.accountservice.repository;

import com.example.accountservice.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CuentaRepository extends JpaRepository<Account, Long> {

    List<Account> findAllByClienteId(Long clienteId);

    List<Account> findAllByClienteIdAndEstadoTrue(Long clienteId);

    boolean existsByClienteIdAndEstadoTrue(Long clienteId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Cuenta c SET c.estado = false WHERE c.clienteId = :clienteId AND c.estado = true")
    void desactivarCuentasActivasPorCliente(Long clienteId);
}
