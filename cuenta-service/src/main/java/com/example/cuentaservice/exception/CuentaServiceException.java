package com.example.cuentaservice.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CuentaServiceException extends RuntimeException {

    public static final String CUENTA_NOT_FOUND = "Cuenta no encontrada.";
    public static final String CUENTA_CREATION_ERROR = "Error al crear la cuenta.";
    public static final String CUENTA_UPDATE_ERROR = "Error al actualizar la cuenta.";
    public static final String CUENTA_DELETION_ERROR = "Error al eliminar la cuenta.";
    public static final String INSUFFICIENT_BALANCE = "Saldo no disponible.";
    public static final String INACTIVE_ACCOUNT = "La cuenta está inactiva y no se pueden realizar operaciones.";
    public static final String INACTIVE_CUSTOMER = "Cliente inactivo no se puede crear cuenta.";
    public static final String CUSTOMER_NOT_FOUND = "No existe este cliente para vincular a la cuenta.";

    public CuentaServiceException(String message) {
        super(message);
    }

    public CuentaServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
