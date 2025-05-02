package com.example.accountservice.repository;

import com.example.accountservice.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findAllByCustomerId(Long customerId);

    List<Account> findAllByCustomerIdAndStatusTrue(Long customerId);

    boolean existsByCustomerIdAndStatusTrue(Long customerId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Account a SET a.status = false WHERE a.customerId = :customerId AND a.status = true")
    void deactivateActiveAccountsByCustomer(Long customerId);
}
