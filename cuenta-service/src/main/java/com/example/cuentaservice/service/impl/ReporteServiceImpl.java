package com.example.cuentaservice.service.impl;

import com.example.cuentaservice.dto.response.MovimientoResponseDTO;
import com.example.cuentaservice.dto.response.ReporteResponseDTO;
import com.example.cuentaservice.entity.Cuenta;
import com.example.cuentaservice.entity.Movimiento;
import com.example.cuentaservice.exception.ReporteServiceException;
import com.example.cuentaservice.repository.CuentaRepository;
import com.example.cuentaservice.repository.MovimientoRepository;
import com.example.cuentaservice.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.cuentaservice.exception.ReporteServiceException.CLIENTE_SIN_CUENTAS;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final CuentaRepository cuentaRepository;
    private final MovimientoRepository movimientoRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<ReporteResponseDTO> getReporte(LocalDate fechaInicio, LocalDate fechaFin, Long clienteId) {
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(23, 59, 59);

        List<Cuenta> cuentas = cuentaRepository.findAllByClienteId(clienteId);
        if (cuentas.isEmpty()) {
            throw new ReporteServiceException(CLIENTE_SIN_CUENTAS);
        }

        return cuentas.stream()
                .map(cuenta -> buildReporteResponseDTO(cuenta, inicio, fin))
                .collect(Collectors.toList());
    }

    private ReporteResponseDTO buildReporteResponseDTO(Cuenta cuenta, LocalDateTime inicio, LocalDateTime fin) {
        List<Movimiento> movimientos = movimientoRepository.findAllByCuenta_CuentaIdAndFechaBetween(
                cuenta.getCuentaId(), inicio, fin);

        List<MovimientoResponseDTO> movimientosDTO = movimientos.stream()
                .map(mov -> {
                    MovimientoResponseDTO dto = modelMapper.map(mov, MovimientoResponseDTO.class);
                    dto.setNumeroCuenta(cuenta.getNumeroCuenta());
                    return dto;
                })
                .collect(Collectors.toList());

        ReporteResponseDTO reporte = new ReporteResponseDTO();
        reporte.setNumeroCuenta(cuenta.getNumeroCuenta());
        reporte.setSaldoInicial(cuenta.getSaldoInicial());
        reporte.setSaldoDisponible(cuenta.getSaldoDisponible());
        reporte.setMovimientos(movimientosDTO);

        return reporte;
    }
}
