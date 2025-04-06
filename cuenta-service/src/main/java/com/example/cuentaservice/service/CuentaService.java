package com.example.cuentaservice.service;

import com.example.cuentaservice.dto.request.CuentaRequestDTO;
import com.example.cuentaservice.dto.response.CuentaResponseDTO;

import java.util.List;

public interface CuentaService {
    List<CuentaResponseDTO> findAll();
    CuentaResponseDTO findById(Long id);
    CuentaResponseDTO create(CuentaRequestDTO cuentaRequestDTO);
    CuentaResponseDTO update(Long id, CuentaRequestDTO cuentaRequestDTO);
    void delete(Long id);
}
