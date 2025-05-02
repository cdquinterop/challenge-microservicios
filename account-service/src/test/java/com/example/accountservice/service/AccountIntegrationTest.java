package com.example.accountservice.service;

import com.example.accountservice.AccountServiceApplication;
import com.example.accountservice.dto.event.CustomerEventDTO;
import com.example.accountservice.dto.request.AccountRequestDTO;
import com.example.accountservice.dto.response.AccountResponseDTO;
import com.example.accountservice.entity.Customer;
import com.example.accountservice.exception.AccountServiceException;
import com.example.accountservice.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.awaitility.Awaitility.await;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AccountServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AccountIntegrationTest {

    @Autowired private RabbitTemplate rabbitTemplate;
    @Autowired private Queue customerQueue;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private AccountService accountService;
    @Autowired private ObjectMapper objectMapper;

    @Test
    public void testCustomerCreationAndAccountCreation() {
        long customerId = generateUniqueCustomerId();
        CustomerEventDTO customerEvent = new CustomerEventDTO();
        customerEvent.setAction("CREATE");
        customerEvent.setCustomerId(customerId);
        customerEvent.setName("John Doe");
        customerEvent.setStatus(true);

        rabbitTemplate.convertAndSend(customerQueue.getName(), customerEvent);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            Customer customer = customerRepository.findByCustomerId(customerId)
                    .orElseThrow(() -> new AssertionError("Customer was not persisted"));
            assertEquals("John Doe", customer.getName());
            assertTrue(customer.getStatus());
        });

        AccountRequestDTO requestDTO = new AccountRequestDTO();
        requestDTO.setCustomerId(customerId);
        requestDTO.setInitialBalance(1000.0);
        requestDTO.setAccountType("SAVINGS");

        AccountResponseDTO responseDTO = accountService.create(requestDTO);

        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getAccountNumber());
        assertTrue(responseDTO.getStatus());
        assertEquals(1000.0, responseDTO.getInitialBalance());
    }

    @Test
    public void testInactiveCustomerCannotCreateAccount() {
        long customerId = generateUniqueCustomerId();

        CustomerEventDTO activeCustomer = new CustomerEventDTO();
        activeCustomer.setAction("CREATE");
        activeCustomer.setCustomerId(customerId);
        activeCustomer.setName("Inactive Customer");
        activeCustomer.setStatus(true);

        rabbitTemplate.convertAndSend(customerQueue.getName(), activeCustomer);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            Customer customer = customerRepository.findByCustomerId(customerId)
                    .orElseThrow(() -> new AssertionError("Customer not yet created"));
            assertTrue(customer.getStatus());
        });

        CustomerEventDTO deactivateCustomer = new CustomerEventDTO();
        deactivateCustomer.setAction("DELETE");
        deactivateCustomer.setCustomerId(customerId);

        rabbitTemplate.convertAndSend(customerQueue.getName(), deactivateCustomer);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            Customer customer = customerRepository.findByCustomerId(customerId)
                    .orElseThrow(() -> new AssertionError("Customer not found after DELETE event"));
            assertFalse(customer.getStatus(), "Customer should be inactive");
        });

        AccountRequestDTO requestDTO = new AccountRequestDTO();
        requestDTO.setCustomerId(customerId);
        requestDTO.setInitialBalance(500.0);

        AccountServiceException exception = assertThrows(AccountServiceException.class, () -> {
            accountService.create(requestDTO);
        });

        assertEquals(AccountServiceException.INACTIVE_CUSTOMER, exception.getMessage());
    }

    private long generateUniqueCustomerId() {
        return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }
}
