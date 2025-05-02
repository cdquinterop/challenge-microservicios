package com.example.accountservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AccountServiceException extends RuntimeException {

    public static final String ACCOUNT_NOT_FOUND = "Account not found.";
    public static final String CUSTOMER_NOT_FOUND = "Customer does not exist to link to the account.";
    public static final String INACTIVE_CUSTOMER = "Inactive customer, cannot create account.";
    public static final String INACTIVE_ACCOUNT = "Account is inactive, operations are not allowed.";
    public static final String INSUFFICIENT_BALANCE = "Insufficient balance.";

    public static final String ACCOUNT_CREATION_ERROR = "Error while creating account.";
    public static final String ACCOUNT_UPDATE_ERROR = "Error while updating account.";
    public static final String ACCOUNT_DELETION_ERROR = "Error while deleting account.";

    public AccountServiceException(String message) {
        super(message);
    }

    public AccountServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
