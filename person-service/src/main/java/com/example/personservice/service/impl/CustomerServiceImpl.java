package com.example.personservice.service.impl;

import com.example.personservice.dto.request.CustomerRequestDTO;
import com.example.personservice.dto.response.CustomerResponseDTO;
import com.example.personservice.entity.Customer;
import com.example.personservice.exception.CustomerServiceException;
import com.example.personservice.messaging.CustomerEventPublisher;
import com.example.personservice.repository.CustomerRepository;
import com.example.personservice.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CustomerEventPublisher customerEventPublisher;

    @Override
    public List<CustomerResponseDTO> findAll() {
        return customerRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponseDTO findById(Long id) {
        return toResponse(findCustomerOrThrow(id));
    }

    @Override
    public CustomerResponseDTO create(CustomerRequestDTO dto) {
        validateData(dto);

        if (customerRepository.findByIdentification(dto.getIdentification()).isPresent()) {
            throw new CustomerServiceException(CustomerServiceException.CUSTOMER_ALREADY_REGISTERED + dto.getIdentification());
        }

        Customer customer = toEntity(dto);
        customer.setPassword(passwordEncoder.encode(dto.getPassword()));

        Customer saved = saveCustomer(customer, "CREATE");
        return toResponse(saved);
    }

    @Override
    public CustomerResponseDTO update(Long id, CustomerRequestDTO dto) {
        Customer existing = findCustomerOrThrow(id);

        Customer updated = toEntity(dto);
        updated.setCustomerId(existing.getCustomerId());
        updated.setPassword(getUpdatedPassword(dto.getPassword(), existing.getPassword()));

        Customer saved = saveCustomer(updated, "UPDATE");
        return toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        Customer customer = findCustomerOrThrow(id);
        customer.setStatus(false);
        saveCustomer(customer, "DELETE");
    }

    private Customer findCustomerOrThrow(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerServiceException(CustomerServiceException.CUSTOMER_NOT_FOUND + " with ID: " + id));
    }

    private Customer saveCustomer(Customer customer, String action) {
        try {
            Customer saved = customerRepository.save(customer);
            customerEventPublisher.publishEvent(action, saved);
            return saved;
        } catch (Exception ex) {
            log.error("Error saving customer. Action: {}, ID: {}, Error: {}", action, customer.getCustomerId(), ex.getMessage(), ex);
            throw new CustomerServiceException(errorMessageByAction(action), ex);
        }
    }

    private String getUpdatedPassword(String newPassword, String currentPassword) {
        return (newPassword != null && !newPassword.isBlank()) ? passwordEncoder.encode(newPassword) : currentPassword;
    }

    private void validateData(CustomerRequestDTO dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException(CustomerServiceException.CUSTOMER_NAME_EMPTY);
        }
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException(CustomerServiceException.CUSTOMER_PASSWORD_REQUIRED);
        }
    }

    private Customer toEntity(CustomerRequestDTO dto) {
        return modelMapper.map(dto, Customer.class);
    }

    private CustomerResponseDTO toResponse(Customer customer) {
        return modelMapper.map(customer, CustomerResponseDTO.class);
    }

    private String errorMessageByAction(String action) {
        return switch (action) {
            case "CREATE" -> CustomerServiceException.CUSTOMER_CREATION_ERROR;
            case "UPDATE" -> CustomerServiceException.CUSTOMER_UPDATE_ERROR;
            case "DELETE" -> CustomerServiceException.CUSTOMER_DELETION_ERROR;
            default -> "Unknown error in customer operation.";
        };
    }
}
