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

import static com.example.cuentaservice.exception.CuentaServiceException.*;
import static com.example.cuentaservice.exception.MovimientoServiceException.*;

@Service
@RequiredArgsConstructor
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;
    private final ModelMapper modelMapper;

    private static final String DEPOSITO = "DEPOSITO";
    private static final String RETIRO = "RETIRO";

    @Override
    public List<MovimientoResponseDTO> findAll() {
        return movimientoRepository.findAll().stream()
                .map(mov -> {
                    MovimientoResponseDTO dto = modelMapper.map(mov, MovimientoResponseDTO.class);
                    dto.setNumeroCuenta(mov.getCuenta().getNumeroCuenta());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MovimientoResponseDTO create(MovimientoRequestDTO dto) {
        Cuenta cuenta = obtenerCuentaActiva(dto.getCuentaId());
        validarMovimiento(dto);

        double nuevoSaldo = calcularNuevoSaldo(dto.getTipoMovimiento(), dto.getMonto(), cuenta.getSaldoDisponible());

        Movimiento movimiento = new Movimiento();
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setTipoMovimiento(dto.getTipoMovimiento().toUpperCase());
        movimiento.setValor(obtenerValorConSigno(dto.getTipoMovimiento(), dto.getMonto()));
        movimiento.setSaldo(nuevoSaldo);
        movimiento.setCuenta(cuenta);

        Movimiento saved = movimientoRepository.save(movimiento);

        cuenta.setSaldoDisponible(nuevoSaldo);
        cuentaRepository.save(cuenta);

        return modelMapper.map(saved, MovimientoResponseDTO.class);
    }

    @Override
    @Transactional
    public MovimientoResponseDTO update(Long id, MovimientoRequestDTO dto) {
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new MovimientoServiceException(MOVIMIENTO_NOT_FOUND));

        Cuenta cuenta = obtenerCuentaActiva(dto.getCuentaId());
        validarMovimiento(dto);

        double nuevoSaldo = calcularNuevoSaldo(dto.getTipoMovimiento(), dto.getMonto(), cuenta.getSaldoDisponible());

        movimiento.setFecha(LocalDateTime.now());
        movimiento.setTipoMovimiento(dto.getTipoMovimiento().toUpperCase());
        movimiento.setValor(obtenerValorConSigno(dto.getTipoMovimiento(), dto.getMonto()));
        movimiento.setSaldo(nuevoSaldo);
        movimiento.setCuenta(cuenta);

        Movimiento updated = movimientoRepository.save(movimiento);

        cuenta.setSaldoDisponible(nuevoSaldo);
        cuentaRepository.save(cuenta);

        return modelMapper.map(updated, MovimientoResponseDTO.class);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new MovimientoServiceException(MOVIMIENTO_NOT_FOUND));

        Cuenta cuenta = movimiento.getCuenta();
        double nuevoSaldo = cuenta.getSaldoDisponible() - movimiento.getValor();

        movimientoRepository.delete(movimiento);

        cuenta.setSaldoDisponible(nuevoSaldo);
        cuentaRepository.save(cuenta);
    }


    private Cuenta obtenerCuentaActiva(Long cuentaId) {
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new CuentaServiceException(CUENTA_NOT_FOUND));

        if (!Boolean.TRUE.equals(cuenta.getEstado())) {
            throw new CuentaServiceException(INACTIVE_ACCOUNT);
        }
        return cuenta;
    }

    private void validarMovimiento(MovimientoRequestDTO dto) {
        String tipo = dto.getTipoMovimiento().toUpperCase();

        if (!List.of(DEPOSITO, RETIRO).contains(tipo)) {
            throw new MovimientoServiceException(INVALID_MOVEMENT_TYPE);
        }

        if (dto.getMonto() == null || dto.getMonto() <= 0) {
            throw new MovimientoServiceException(INVALID_MOVEMENT_VALUE);
        }
    }

    private double calcularNuevoSaldo(String tipoMovimiento, Double monto, Double saldoActual) {
        String tipo = tipoMovimiento.toUpperCase();

        if (RETIRO.equals(tipo)) {
            double nuevoSaldo = saldoActual - monto;
            if (nuevoSaldo < 0) {
                throw new CuentaServiceException(INSUFFICIENT_BALANCE);
            }
            return nuevoSaldo;
        }
        return saldoActual + monto;
    }

    private double obtenerValorConSigno(String tipoMovimiento, Double monto) {
        return RETIRO.equalsIgnoreCase(tipoMovimiento) ? -monto : monto;
    }
}
