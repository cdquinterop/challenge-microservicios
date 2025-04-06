package com.example.cuentaservice.repository;

import com.example.cuentaservice.entity.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

    List<Cuenta> findAllByClienteId(Long clienteId);

    List<Cuenta> findAllByClienteIdAndEstadoTrue(Long clienteId);

    boolean existsByClienteIdAndEstadoTrue(Long clienteId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Cuenta c SET c.estado = false WHERE c.clienteId = :clienteId AND c.estado = true")
    void desactivarCuentasActivasPorCliente(Long clienteId);
}
