package com.example.cuentaservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CuentaServiceException.class)
    public ResponseEntity<String> handleCuentaServiceException(CuentaServiceException ex) {
        return handleBadRequest(ex.getMessage());
    }

    @ExceptionHandler(MovimientoServiceException.class)
    public ResponseEntity<String> handleMovimientoServiceException(MovimientoServiceException ex) {
        return handleBadRequest(ex.getMessage());
    }

    @ExceptionHandler(ReporteServiceException.class)
    public ResponseEntity<String> handleReporteServiceException(ReporteServiceException ex) {
        return handleBadRequest(ex.getMessage());
    }

    // Manejo de errores genéricos no controlados
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ha ocurrido un error inesperado: " + ex.getMessage());
    }

    private ResponseEntity<String> handleBadRequest(String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }
}
