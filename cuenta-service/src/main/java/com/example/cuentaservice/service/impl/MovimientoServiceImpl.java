package com.example.cuentaservice.service.impl;

import com.example.cuentaservice.dto.request.MovimientoRequestDTO;
import com.example.cuentaservice.dto.response.MovimientoResponseDTO;
import com.example.cuentaservice.entity.Cuenta;
import com.example.cuentaservice.entity.Movimiento;
import com.example.cuentaservice.exception.CuentaServiceException;
import com.example.cuentaservice.exception.MovimientoServiceException;
import com.example.cuentaservice.repository.CuentaRepository;
import com.example.cuentaservice.repository.MovimientoRepository;
import com.example.cuentaservice.service.MovimientoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public List<MovimientoResponseDTO> findAll() {
        try {
            return movimientoRepository.findAll().stream()
                    .map(movimiento -> {
                        MovimientoResponseDTO dto = modelMapper.map(movimiento, MovimientoResponseDTO.class);
                        dto.setNumeroCuenta(movimiento.getCuenta().getNumeroCuenta());
                        return dto;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new MovimientoServiceException("Error al obtener los movimientos.", e);
        }
    }

    @Override
    @Transactional
    public MovimientoResponseDTO create(MovimientoRequestDTO movimientoRequestDTO) {
        try {
            Cuenta cuenta = cuentaRepository.findById(movimientoRequestDTO.getCuentaId())
                    .orElseThrow(() -> new CuentaServiceException(CuentaServiceException.CUENTA_NOT_FOUND));

            if (!cuenta.getEstado()) {

                throw new CuentaServiceException(CuentaServiceException.INACTIVE_ACCOUNT);
            }

            String tipoMovimiento = movimientoRequestDTO.getTipoMovimiento().toUpperCase();
            if (!List.of("DEPOSITO", "RETIRO").contains(tipoMovimiento)) {
                throw new MovimientoServiceException(MovimientoServiceException.INVALID_MOVEMENT_TYPE);
            }

            if (movimientoRequestDTO.getMonto() <= 0) {
                throw new MovimientoServiceException(MovimientoServiceException.INVALID_MOVEMENT_VALUE);
            }

            Double nuevoSaldo;
            if (tipoMovimiento.equals("RETIRO")) {
                nuevoSaldo = cuenta.getSaldoDisponible() - movimientoRequestDTO.getMonto();
                if (nuevoSaldo < 0) {
                    throw new CuentaServiceException(CuentaServiceException.INSUFFICIENT_BALANCE);
                }
            } else {
                nuevoSaldo = cuenta.getSaldoDisponible() + movimientoRequestDTO.getMonto();
            }

            Movimiento movimiento = new Movimiento();
            movimiento.setFecha(LocalDateTime.now());
            movimiento.setTipoMovimiento(tipoMovimiento);
            movimiento.setValor(tipoMovimiento.equals("RETIRO") ? -movimientoRequestDTO.getMonto() : movimientoRequestDTO.getMonto());
            movimiento.setSaldo(nuevoSaldo);
            movimiento.setCuenta(cuenta);

            Movimiento savedMovimiento = movimientoRepository.save(movimiento);

            cuenta.setSaldoDisponible(nuevoSaldo);
            cuentaRepository.save(cuenta);

            return modelMapper.map(savedMovimiento, MovimientoResponseDTO.class);
        } catch (CuentaServiceException | MovimientoServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new MovimientoServiceException(MovimientoServiceException.MOVIMIENTO_CREATION_ERROR, e);
        }
    }



    @Override
    @Transactional
    public MovimientoResponseDTO update(Long id, MovimientoRequestDTO movimientoRequestDTO) {
        try {
            Movimiento movimiento = movimientoRepository.findById(id)
                    .orElseThrow(() -> new MovimientoServiceException("Movimiento no encontrado"));

            Cuenta cuenta = cuentaRepository.findById(movimientoRequestDTO.getCuentaId())
                    .orElseThrow(() -> new CuentaServiceException(CuentaServiceException.CUENTA_NOT_FOUND));

            if (!cuenta.getEstado()) {
                throw new CuentaServiceException(CuentaServiceException.INACTIVE_ACCOUNT);
            }

            String tipoMovimiento = movimientoRequestDTO.getTipoMovimiento().toUpperCase();
            if (!List.of("DEPOSITO", "RETIRO").contains(tipoMovimiento)) {
                throw new MovimientoServiceException(MovimientoServiceException.INVALID_MOVEMENT_TYPE);
            }

            if (movimientoRequestDTO.getMonto() <= 0) {
                throw new MovimientoServiceException(MovimientoServiceException.INVALID_MOVEMENT_VALUE);
            }

            Double nuevoSaldo;
            if (tipoMovimiento.equals("RETIRO")) {
                nuevoSaldo = cuenta.getSaldoDisponible() - movimientoRequestDTO.getMonto();
                if (nuevoSaldo < 0) {
                    throw new CuentaServiceException(CuentaServiceException.INSUFFICIENT_BALANCE);
                }
            } else {
                nuevoSaldo = cuenta.getSaldoDisponible() + movimientoRequestDTO.getMonto();
            }

            movimiento.setFecha(LocalDateTime.now());
            movimiento.setTipoMovimiento(tipoMovimiento);
            movimiento.setValor(tipoMovimiento.equals("RETIRO") ? -movimientoRequestDTO.getMonto() : movimientoRequestDTO.getMonto());
            movimiento.setSaldo(nuevoSaldo);
            movimiento.setCuenta(cuenta);

            Movimiento updatedMovimiento = movimientoRepository.save(movimiento);

            cuenta.setSaldoDisponible(nuevoSaldo);
            cuentaRepository.save(cuenta);

            return modelMapper.map(updatedMovimiento, MovimientoResponseDTO.class);
        } catch (CuentaServiceException | MovimientoServiceException e) {
            throw new MovimientoServiceException("Error al actualizar el movimiento: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new MovimientoServiceException(MovimientoServiceException.MOVIMIENTO_UPDATE_ERROR, e);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        try {
            Movimiento movimiento = movimientoRepository.findById(id)
                    .orElseThrow(() -> new MovimientoServiceException(MovimientoServiceException.MOVIMIENTO_NOT_FOUND));

            Cuenta cuenta = movimiento.getCuenta();
            Double nuevoSaldo = cuenta.getSaldoDisponible() - movimiento.getValor();

            movimientoRepository.delete(movimiento);

            cuenta.setSaldoDisponible(nuevoSaldo);
            cuentaRepository.save(cuenta);
        } catch (Exception e) {
            throw new MovimientoServiceException("Error al eliminar el movimiento.", e);
        }
    }

    private boolean isValidMovementType(String tipoMovimiento) {
        return tipoMovimiento.equals("DEPOSITO") || tipoMovimiento.equals("RETIRO");
    }
}
