package com.example.accountservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ReporteServiceException extends RuntimeException {


    public static final String INVALID_DATE_FORMAT = "Formato de fecha inválido. Use el formato yyyy-MM-dd.";


    public static final String CLIENTE_SIN_CUENTAS = "No se encontraron cuentas asociadas al cliente.";


    public static final String REPORT_GENERATION_ERROR = "Error al generar el reporte.";

    public ReporteServiceException(String message) {
        super(message);
    }

    public ReporteServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
