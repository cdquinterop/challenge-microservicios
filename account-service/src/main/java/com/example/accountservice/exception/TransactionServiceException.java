package com.example.accountservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TransactionServiceException extends RuntimeException {

    public static final String INVALID_TRANSACTION_TYPE = "Invalid transaction type. Allowed values are: DEPOSIT, WITHDRAWAL.";
    public static final String INVALID_TRANSACTION_AMOUNT = "Transaction amount must be greater than zero.";

    public static final String TRANSACTION_NOT_FOUND = "Transaction not found.";

    public static final String TRANSACTION_CREATION_ERROR = "Error while creating transaction.";
    public static final String TRANSACTION_UPDATE_ERROR = "Error while updating transaction.";

    public TransactionServiceException(String message) {
        super(message);
    }

    public TransactionServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
