package com.example.accountservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountServiceException.class)
    public ResponseEntity<String> handleAccountServiceException(AccountServiceException ex) {
        return handleBadRequest(ex.getMessage());
    }

    @ExceptionHandler(TransactionServiceException.class)
    public ResponseEntity<String> handleTransactionServiceException(TransactionServiceException ex) {
        return handleBadRequest(ex.getMessage());
    }

    @ExceptionHandler(ReportServiceException.class)
    public ResponseEntity<String> handleReportServiceException(ReportServiceException ex) {
        return handleBadRequest(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred: " + ex.getMessage());
    }

    private ResponseEntity<String> handleBadRequest(String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }
}
