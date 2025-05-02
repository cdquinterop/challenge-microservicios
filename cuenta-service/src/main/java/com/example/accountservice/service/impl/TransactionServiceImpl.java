package com.example.accountservice.service.impl;

import com.example.accountservice.dto.request.TransactionRequestDTO;
import com.example.accountservice.dto.response.TransactionResponseDTO;
import com.example.accountservice.entity.Account;
import com.example.accountservice.entity.Transaction;
import com.example.accountservice.exception.AccountServiceException;
import com.example.accountservice.exception.TransactionServiceException;
import com.example.accountservice.repository.AccountRepository;
import com.example.accountservice.repository.TransactionRepository;
import com.example.accountservice.service.MovimientoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.accountservice.exception.AccountServiceException.*;

@Service
@RequiredArgsConstructor
public class MovimientoServiceImpl implements MovimientoService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;

    private static final String DEPOSITO = "DEPOSITO";
    private static final String RETIRO = "RETIRO";

    @Override
    public List<TransactionResponseDTO> findAll() {
        return transactionRepository.findAll().stream()
                .map(mov -> {
                    TransactionResponseDTO dto = modelMapper.map(mov, TransactionResponseDTO.class);
                    dto.setNumeroCuenta(mov.getAccount().getNumeroCuenta());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TransactionResponseDTO create(TransactionRequestDTO dto) {
        Account account = obtenerCuentaActiva(dto.getCuentaId());
        validarMovimiento(dto);

        double nuevoSaldo = calcularNuevoSaldo(dto.getTipoMovimiento(), dto.getMonto(), account.getSaldoDisponible());

        Transaction transaction = new Transaction();
        transaction.setFecha(LocalDateTime.now());
        transaction.setTipoMovimiento(dto.getTipoMovimiento().toUpperCase());
        transaction.setValor(obtenerValorConSigno(dto.getTipoMovimiento(), dto.getMonto()));
        transaction.setSaldo(nuevoSaldo);
        transaction.setAccount(account);

        Transaction saved = transactionRepository.save(transaction);

        account.setSaldoDisponible(nuevoSaldo);
        accountRepository.save(account);

        return modelMapper.map(saved, TransactionResponseDTO.class);
    }

    @Override
    @Transactional
    public TransactionResponseDTO update(Long id, TransactionRequestDTO dto) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionServiceException(MOVIMIENTO_NOT_FOUND));

        Account account = obtenerCuentaActiva(dto.getCuentaId());
        validarMovimiento(dto);

        double nuevoSaldo = calcularNuevoSaldo(dto.getTipoMovimiento(), dto.getMonto(), account.getSaldoDisponible());

        transaction.setFecha(LocalDateTime.now());
        transaction.setTipoMovimiento(dto.getTipoMovimiento().toUpperCase());
        transaction.setValor(obtenerValorConSigno(dto.getTipoMovimiento(), dto.getMonto()));
        transaction.setSaldo(nuevoSaldo);
        transaction.setAccount(account);

        Transaction updated = transactionRepository.save(transaction);

        account.setSaldoDisponible(nuevoSaldo);
        accountRepository.save(account);

        return modelMapper.map(updated, TransactionResponseDTO.class);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionServiceException(MOVIMIENTO_NOT_FOUND));

        Account account = transaction.getAccount();
        double nuevoSaldo = account.getSaldoDisponible() - transaction.getValor();

        transactionRepository.delete(transaction);

        account.setSaldoDisponible(nuevoSaldo);
        accountRepository.save(account);
    }


    private Account obtenerCuentaActiva(Long cuentaId) {
        Account account = accountRepository.findById(cuentaId)
                .orElseThrow(() -> new AccountServiceException(CUENTA_NOT_FOUND));

        if (!Boolean.TRUE.equals(account.getEstado())) {
            throw new AccountServiceException(INACTIVE_ACCOUNT);
        }
        return account;
    }

    private void validarMovimiento(TransactionRequestDTO dto) {
        String tipo = dto.getTipoMovimiento().toUpperCase();

        if (!List.of(DEPOSITO, RETIRO).contains(tipo)) {
            throw new TransactionServiceException(INVALID_MOVEMENT_TYPE);
        }

        if (dto.getMonto() == null || dto.getMonto() <= 0) {
            throw new TransactionServiceException(INVALID_MOVEMENT_VALUE);
        }
    }

    private double calcularNuevoSaldo(String tipoMovimiento, Double monto, Double saldoActual) {
        String tipo = tipoMovimiento.toUpperCase();

        if (RETIRO.equals(tipo)) {
            double nuevoSaldo = saldoActual - monto;
            if (nuevoSaldo < 0) {
                throw new AccountServiceException(INSUFFICIENT_BALANCE);
            }
            return nuevoSaldo;
        }
        return saldoActual + monto;
    }

    private double obtenerValorConSigno(String tipoMovimiento, Double monto) {
        return RETIRO.equalsIgnoreCase(tipoMovimiento) ? -monto : monto;
    }
}
