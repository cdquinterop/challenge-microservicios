package com.example.cuentaservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ReporteServiceException extends RuntimeException {

    public ReporteServiceException(String message) {
        super(message);
    }

    public ReporteServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
