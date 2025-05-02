package com.example.accountservice.messaging;

import com.example.accountservice.config.RabbitMQConfig;
import com.example.accountservice.dto.event.CustomerEventDTO;
import com.example.accountservice.entity.Customer;
import com.example.accountservice.entity.Account;
import com.example.accountservice.repository.CustomerRepository;
import com.example.accountservice.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerMessageListener {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    @RabbitListener(queues = RabbitMQConfig.CUSTOMER_QUEUE)
    public void handleCustomerEvent(CustomerEventDTO event) {
        log.info(" Event received from RabbitMQ: {}", event);

        if (event == null || event.getCustomerId() == null || event.getAction() == null) {
            log.warn(" Ignored event due to missing fields: {}", event);
            return;
        }

        switch (event.getAction().toUpperCase()) {
            case "CREATE" -> processCreate(event);
            case "UPDATE" -> processUpdate(event);
            case "DELETE" -> processDelete(event);
            default -> log.warn(" Unrecognized action: {}", event.getAction());
        }
    }

    @Transactional
    protected void processCreate(CustomerEventDTO event) {
        Customer customer = mapToEntity(event);
        customerRepository.save(customer);
        log.info(" Customer created: {}", customer);
    }

    @Transactional
    protected void processUpdate(CustomerEventDTO event) {
        Customer customer = customerRepository.findByCustomerId(event.getCustomerId())
                .orElseGet(() -> {
                    log.warn(" Customer not found, creating new for update.");
                    return new Customer();
                });

        customer.setCustomerId(event.getCustomerId());
        customer.setName(event.getName());
        customer.setStatus(event.getStatus());

        customerRepository.save(customer);
        log.info(" Customer updated: {}", customer);
    }

    @Transactional
    protected void processDelete(CustomerEventDTO event) {
        customerRepository.findByCustomerId(event.getCustomerId()).ifPresent(customer -> {
            customer.setStatus(false);
            customerRepository.save(customer);
            log.info(" Customer deactivated: {}", customer);
        });

        List<Account> accounts = accountRepository.findAllByCustomerId(event.getCustomerId());
        accounts.forEach(account -> {
            account.setStatus(false);
            accountRepository.save(account);
        });

        log.info(" {} accounts deactivated for customer {}", accounts.size(), event.getCustomerId());
    }

    private Customer mapToEntity(CustomerEventDTO dto) {
        Customer customer = new Customer();
        customer.setCustomerId(dto.getCustomerId());
        customer.setName(dto.getName());
        customer.setStatus(dto.getStatus());
        return customer;
    }
}
