package com.example.personservice.service;

import com.example.personservice.dto.request.CustomerRequestDTO;
import com.example.personservice.dto.response.CustomerResponseDTO;

import java.util.List;

public interface CustomerService {
    List<CustomerResponseDTO> findAll();
    CustomerResponseDTO findById(Long id);
    CustomerResponseDTO create(CustomerRequestDTO customerRequestDTO);
    CustomerResponseDTO update(Long id, CustomerRequestDTO customerRequestDTO);
    void delete(Long id);
}
