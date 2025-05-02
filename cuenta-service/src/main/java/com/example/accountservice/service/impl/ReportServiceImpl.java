package com.example.accountservice.service.impl;

import com.example.accountservice.dto.response.TransactionResponseDTO;
import com.example.accountservice.dto.response.ReportResponseDTO;
import com.example.accountservice.entity.Account;
import com.example.accountservice.entity.Transaction;
import com.example.accountservice.exception.ReportServiceException;
import com.example.accountservice.repository.AccountRepository;
import com.example.accountservice.repository.TransactionRepository;
import com.example.accountservice.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.accountservice.exception.ReportServiceException.CLIENTE_SIN_CUENTAS;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<ReportResponseDTO> getReporte(LocalDate fechaInicio, LocalDate fechaFin, Long clienteId) {
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(23, 59, 59);

        List<Account> accounts = accountRepository.findAllByClienteId(clienteId);
        if (accounts.isEmpty()) {
            throw new ReportServiceException(CLIENTE_SIN_CUENTAS);
        }

        return accounts.stream()
                .map(cuenta -> buildReporteResponseDTO(cuenta, inicio, fin))
                .collect(Collectors.toList());
    }

    private ReportResponseDTO buildReporteResponseDTO(Account account, LocalDateTime inicio, LocalDateTime fin) {
        List<Transaction> transactions = transactionRepository.findAllByCuenta_CuentaIdAndFechaBetween(
                account.getCuentaId(), inicio, fin);

        List<TransactionResponseDTO> movimientosDTO = transactions.stream()
                .map(mov -> {
                    TransactionResponseDTO dto = modelMapper.map(mov, TransactionResponseDTO.class);
                    dto.setNumeroCuenta(account.getNumeroCuenta());
                    return dto;
                })
                .collect(Collectors.toList());

        ReportResponseDTO reporte = new ReportResponseDTO();
        reporte.setNumeroCuenta(account.getNumeroCuenta());
        reporte.setSaldoInicial(account.getSaldoInicial());
        reporte.setSaldoDisponible(account.getSaldoDisponible());
        reporte.setMovimientos(movimientosDTO);

        return reporte;
    }
}
