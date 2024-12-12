package com.example.cuentaservice.service.impl;

import com.example.cuentaservice.cache.ClienteCache;
import com.example.cuentaservice.dto.cache.ClienteCacheDTO;
import com.example.cuentaservice.dto.request.CuentaRequestDTO;
import com.example.cuentaservice.dto.response.CuentaResponseDTO;
import com.example.cuentaservice.entity.Cuenta;
import com.example.cuentaservice.exception.CuentaServiceException;
import com.example.cuentaservice.repository.CuentaRepository;
import com.example.cuentaservice.service.CuentaService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CuentaServiceImpl implements CuentaService {

    private final CuentaRepository cuentaRepository;
    private final ClienteCache clienteCache;
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public List<CuentaResponseDTO> findAll() {
        try {
            return cuentaRepository.findAll().stream()
                    .map(cuenta -> modelMapper.map(cuenta, CuentaResponseDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new CuentaServiceException("Error al obtener las cuentas.", e);
        }
    }

    @Override
    public CuentaResponseDTO findById(Long id) {
        try {
            Cuenta cuenta = cuentaRepository.findById(id)
                    .orElseThrow(() -> new CuentaServiceException(CuentaServiceException.CUENTA_NOT_FOUND));
            return modelMapper.map(cuenta, CuentaResponseDTO.class);
        } catch (Exception e) {
            throw new CuentaServiceException("Error al buscar la cuenta.", e);
        }
    }

    @Override
    @Transactional
    public CuentaResponseDTO create(CuentaRequestDTO cuentaRequestDTO) {
        try {
            ClienteCacheDTO cliente = clienteCache.getCliente(cuentaRequestDTO.getClienteId());

            if (cliente == null) {
                List<Cuenta> cuentas = cuentaRepository.findAllByClienteId(cuentaRequestDTO.getClienteId());
                if (!cuentas.isEmpty()) {
                    Cuenta cuentaExistente = cuentas.get(0);
                    cliente = new ClienteCacheDTO();
                    cliente.setClienteId(cuentaExistente.getClienteId());
                    cliente.setEstado(cuentaExistente.getEstado());
                    clienteCache.putCliente(cliente.getClienteId(), cliente);
                } else {
                    throw new CuentaServiceException(CuentaServiceException.CUSTOMER_NOT_FOUND);
                }
            }

            if (!cliente.getEstado()) {
                throw new CuentaServiceException(CuentaServiceException.INACTIVE_CUSTOMER);
            }

            Cuenta cuenta = modelMapper.map(cuentaRequestDTO, Cuenta.class);
            cuenta.setId(null);
            cuenta.setNumeroCuenta(generarNumeroCuenta());
            cuenta.setEstado(true);
            cuenta.setSaldoInicial(cuentaRequestDTO.getSaldoInicial());
            cuenta.setSaldoDisponible(cuentaRequestDTO.getSaldoInicial());

            Cuenta savedCuenta = cuentaRepository.save(cuenta);

            CuentaResponseDTO response = modelMapper.map(savedCuenta, CuentaResponseDTO.class);
            return response;
        } catch (CuentaServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new CuentaServiceException(CuentaServiceException.CUENTA_CREATION_ERROR, e);
        }
    }

    @Override
    @Transactional
    public CuentaResponseDTO update(Long id, CuentaRequestDTO cuentaRequestDTO) {
        try {
            Cuenta existingCuenta = cuentaRepository.findById(id)
                    .orElseThrow(() -> new CuentaServiceException(CuentaServiceException.CUENTA_NOT_FOUND));

            existingCuenta.setTipoCuenta(cuentaRequestDTO.getTipoCuenta());
            existingCuenta.setSaldoInicial(cuentaRequestDTO.getSaldoInicial());

            Cuenta updatedCuenta = cuentaRepository.save(existingCuenta);
            return modelMapper.map(updatedCuenta, CuentaResponseDTO.class);
        } catch (Exception e) {
            throw new CuentaServiceException(CuentaServiceException.CUENTA_UPDATE_ERROR, e);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        try {
            cuentaRepository.deleteById(id);
        } catch (Exception e) {
            throw new CuentaServiceException(CuentaServiceException.CUENTA_DELETION_ERROR, e);
        }
    }

    private String generarNumeroCuenta() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }
}
