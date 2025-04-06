package com.example.cuentaservice.service.impl;

import com.example.cuentaservice.dto.request.CuentaRequestDTO;
import com.example.cuentaservice.dto.response.CuentaResponseDTO;
import com.example.cuentaservice.entity.Cliente;
import com.example.cuentaservice.entity.Cuenta;
import com.example.cuentaservice.exception.CuentaServiceException;
import com.example.cuentaservice.repository.ClienteRepository;
import com.example.cuentaservice.repository.CuentaRepository;
import com.example.cuentaservice.service.CuentaService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static com.example.cuentaservice.exception.CuentaServiceException.*;

@Service
@RequiredArgsConstructor
public class CuentaServiceImpl implements CuentaService {

    private final CuentaRepository cuentaRepository;
    private final ClienteRepository clienteRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<CuentaResponseDTO> findAll() {
        return cuentaRepository.findAll().stream()
                .map(cuenta -> modelMapper.map(cuenta, CuentaResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public CuentaResponseDTO findById(Long id) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new CuentaServiceException(CUENTA_NOT_FOUND));
        return modelMapper.map(cuenta, CuentaResponseDTO.class);
    }

    @Override
    @Transactional
    public CuentaResponseDTO create(CuentaRequestDTO dto) {
        Cliente cliente = clienteRepository.findByClienteId(dto.getClienteId())
                .orElseThrow(() -> new CuentaServiceException(CUSTOMER_NOT_FOUND));

        if (!Boolean.TRUE.equals(cliente.getEstado())) {
            throw new CuentaServiceException(INACTIVE_CUSTOMER);
        }

        Cuenta cuenta = modelMapper.map(dto, Cuenta.class);
        cuenta.setCuentaId(null);
        cuenta.setNumeroCuenta(generarNumeroCuenta());
        cuenta.setEstado(true);
        cuenta.setSaldoInicial(dto.getSaldoInicial());
        cuenta.setSaldoDisponible(dto.getSaldoInicial());

        Cuenta saved = cuentaRepository.save(cuenta);
        return modelMapper.map(saved, CuentaResponseDTO.class);
    }

    @Override
    @Transactional
    public CuentaResponseDTO update(Long id, CuentaRequestDTO dto) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new CuentaServiceException(CUENTA_NOT_FOUND));

        cuenta.setTipoCuenta(dto.getTipoCuenta());
        cuenta.setSaldoInicial(dto.getSaldoInicial());

        Cuenta updated = cuentaRepository.save(cuenta);
        return modelMapper.map(updated, CuentaResponseDTO.class);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!cuentaRepository.existsById(id)) {
            throw new CuentaServiceException(CUENTA_NOT_FOUND);
        }
        cuentaRepository.deleteById(id);
    }

    private String generarNumeroCuenta() {
        return String.format("%06d", 100000 + new Random().nextInt(900000));
    }
}
