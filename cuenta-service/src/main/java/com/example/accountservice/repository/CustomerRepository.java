package com.example.accountservice.repository;

import com.example.accountservice.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByClienteId(Long clienteId);
}
