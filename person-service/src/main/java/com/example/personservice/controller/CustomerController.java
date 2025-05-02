package com.example.personservice.controller;

import com.example.personservice.dto.request.CustomerRequestDTO;
import com.example.personservice.dto.response.CustomerResponseDTO;
import com.example.personservice.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "Get all customers")
    @ApiResponse(responseCode = "200", description = "Successful list")
    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {
        log.info("Fetching all customers");
        return ResponseEntity.ok(customerService.findAll());
    }

    @Operation(summary = "Get a customer by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable Long id) {
        log.info("Searching for customer with ID {}", id);
        return ResponseEntity.ok(customerService.findById(id));
    }

    @Operation(summary = "Create a new customer")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Customer successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid data")
    })
    @PostMapping
    public ResponseEntity<CustomerResponseDTO> createCustomer(@Valid @RequestBody CustomerRequestDTO customerRequestDTO) {
        log.info("Creating customer: {}", customerRequestDTO);
        CustomerResponseDTO customer = customerService.create(customerRequestDTO);
        return ResponseEntity
                .created(URI.create("/api/customers/" + customer.getCustomerId()))
                .body(customer);
    }

    @Operation(summary = "Update an existing customer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer updated"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequestDTO customerRequestDTO) {
        log.info("Updating customer with ID {}: {}", id, customerRequestDTO);
        return ResponseEntity.ok(customerService.update(id, customerRequestDTO));
    }

    @Operation(summary = "Delete a customer by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Customer deleted"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        log.info("Deleting customer with ID {}", id);
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
