package com.example.cuentaservice.service;

import com.example.cuentaservice.dto.request.CuentaRequestDTO;
import com.example.cuentaservice.dto.response.CuentaResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CuentaService {
    List<CuentaResponseDTO> findAll();
    CuentaResponseDTO findById(Long id);
    CuentaResponseDTO create(CuentaRequestDTO cuentaRequestDTO);
    CuentaResponseDTO update(Long id, CuentaRequestDTO cuentaRequestDTO);
    void delete(Long id);
}
