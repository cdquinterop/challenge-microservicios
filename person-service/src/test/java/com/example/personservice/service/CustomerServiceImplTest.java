package com.example.personservice.service;

import com.example.personservice.dto.request.CustomerRequestDTO;
import com.example.personservice.dto.response.CustomerResponseDTO;
import com.example.personservice.entity.Customer;
import com.example.personservice.exception.CustomerServiceException;
import com.example.personservice.messaging.CustomerEventPublisher;
import com.example.personservice.repository.CustomerRepository;
import com.example.personservice.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private CustomerEventPublisher customerEventPublisher;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCustomer_Successful() {
        CustomerRequestDTO request = new CustomerRequestDTO();
        request.setName("John Doe");
        request.setAddress("Fake Street 123");
        request.setIdentification("12345");
        request.setPassword("password");

        Customer customer = new Customer();
        customer.setCustomerId(1L);
        customer.setName("John Doe");
        customer.setAddress("Fake Street 123");
        customer.setStatus(true);

        CustomerResponseDTO responseDTO = new CustomerResponseDTO();
        responseDTO.setCustomerId(1L);
        responseDTO.setName("John Doe");

        when(customerRepository.findByIdentification("12345")).thenReturn(Optional.empty());
        when(modelMapper.map(request, Customer.class)).thenReturn(customer);
        when(passwordEncoder.encode("password")).thenReturn("encoded-password");
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(modelMapper.map(customer, CustomerResponseDTO.class)).thenReturn(responseDTO);

        CustomerResponseDTO response = customerService.create(request);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("John Doe");
        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(customerEventPublisher, times(1)).publishEvent("CREATE", customer);
    }

    @Test
    void testCreateCustomer_NullName() {
        CustomerRequestDTO request = new CustomerRequestDTO();
        request.setAddress("Fake Street 123");
        request.setIdentification("12345");
        request.setPassword("password");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> customerService.create(request));
        assertThat(exception.getMessage()).isEqualTo("Customer name cannot be empty.");
        verifyNoInteractions(customerRepository);
    }

    @Test
    void testCreateCustomer_AlreadyRegistered() {
        CustomerRequestDTO request = new CustomerRequestDTO();
        request.setName("John Doe");
        request.setAddress("Fake Street 123");
        request.setIdentification("12345");
        request.setPassword("password");

        when(customerRepository.findByIdentification("12345")).thenReturn(Optional.of(new Customer()));

        CustomerServiceException exception = assertThrows(CustomerServiceException.class, () -> customerService.create(request));
        assertThat(exception.getMessage()).contains("already registered with identification: 12345");
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testCreateCustomer_RepositoryThrowsException() {
        CustomerRequestDTO request = new CustomerRequestDTO();
        request.setName("John Doe");
        request.setAddress("Fake Street 123");
        request.setIdentification("12345");
        request.setPassword("password");

        Customer customer = new Customer();
        customer.setName("John Doe");

        when(customerRepository.findByIdentification("12345")).thenReturn(Optional.empty());
        when(modelMapper.map(request, Customer.class)).thenReturn(customer);
        when(passwordEncoder.encode("password")).thenReturn("encoded-password");
        when(customerRepository.save(any(Customer.class))).thenThrow(new RuntimeException("Error saving to database"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> customerService.create(request));
        assertThat(exception.getMessage()).isEqualTo("Error saving to database");
        verify(customerRepository, times(1)).save(any(Customer.class));
    }
}
