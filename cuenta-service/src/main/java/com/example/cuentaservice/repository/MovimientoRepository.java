package com.example.cuentaservice.repository;

import com.example.cuentaservice.entity.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    List<Movimiento> findAllByCuentaIdAndFechaBetween(Long cuentaId, LocalDateTime start, LocalDateTime end);
}