package com.example.accountservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MovimientoServiceException extends RuntimeException {


    public static final String INVALID_MOVEMENT_TYPE = "Tipo de movimiento no válido. Los valores permitidos son: DEPOSITO, RETIRO.";
    public static final String INVALID_MOVEMENT_VALUE = "El valor del movimiento debe ser mayor a cero.";


    public static final String MOVIMIENTO_NOT_FOUND = "Movimiento no encontrado.";


    public static final String MOVIMIENTO_CREATION_ERROR = "Error al registrar el movimiento.";
    public static final String MOVIMIENTO_UPDATE_ERROR = "Error al actualizar el movimiento.";

    public MovimientoServiceException(String message) {
        super(message);
    }

    public MovimientoServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
