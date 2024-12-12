package com.example.cuentaservice.service;


import com.example.cuentaservice.dto.request.MovimientoRequestDTO;
import com.example.cuentaservice.dto.response.MovimientoResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MovimientoService {
    List<MovimientoResponseDTO> findAll();
    MovimientoResponseDTO create(MovimientoRequestDTO movimientoRequestDTO);
    MovimientoResponseDTO update(Long id, MovimientoRequestDTO movimientoRequestDTO);
    void delete(Long id);
}
