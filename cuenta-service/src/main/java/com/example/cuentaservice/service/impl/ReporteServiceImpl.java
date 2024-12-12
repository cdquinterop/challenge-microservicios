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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final CuentaRepository cuentaRepository;
    private final MovimientoRepository movimientoRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public List<ReporteResponseDTO> getReporte(String fechaInicio, String fechaFin, Long clienteId) {
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            LocalDateTime inicio = LocalDate.parse(fechaInicio.trim(), dateFormatter).atStartOfDay();
            LocalDateTime fin = LocalDate.parse(fechaFin.trim(), dateFormatter).atTime(23, 59, 59);

            List<Cuenta> cuentas = cuentaRepository.findAllByClienteId(clienteId);

            if (cuentas.isEmpty()) {
                throw new ReporteServiceException("No se encontraron cuentas para el cliente ID " + clienteId);
            }

            return cuentas.stream().map(cuenta -> {
                List<Movimiento> movimientos = movimientoRepository.findAllByCuentaIdAndFechaBetween(
                        cuenta.getId(), inicio, fin);

                List<MovimientoResponseDTO> movimientosDTO = movimientos.stream()
                        .map(movimiento -> modelMapper.map(movimiento, MovimientoResponseDTO.class))
                        .collect(Collectors.toList());

                return buildReporteResponseDTO(cuenta, movimientosDTO);
            }).collect(Collectors.toList());
        } catch (ReporteServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ReporteServiceException("Error al generar el reporte.", e);
        }
    }

    private ReporteResponseDTO buildReporteResponseDTO(Cuenta cuenta, List<MovimientoResponseDTO> movimientosDTO) {
        ReporteResponseDTO reporte = new ReporteResponseDTO();
        reporte.setNumeroCuenta(cuenta.getNumeroCuenta());
        reporte.setSaldoInicial(cuenta.getSaldoInicial());
        reporte.setSaldoDisponible(cuenta.getSaldoDisponible());
        reporte.setMovimientos(movimientosDTO);
        return reporte;
    }
}
