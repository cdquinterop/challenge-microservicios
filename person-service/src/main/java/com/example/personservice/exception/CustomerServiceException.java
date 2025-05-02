package com.example.personservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CustomerServiceException extends RuntimeException {

    public static final String CUSTOMER_NOT_FOUND = "Customer not found.";
    public static final String CUSTOMER_CREATION_ERROR = "Error while creating the customer.";
    public static final String CUSTOMER_UPDATE_ERROR = "Error while updating the customer.";
    public static final String CUSTOMER_DELETION_ERROR = "Error while deleting the customer.";
    public static final String CUSTOMER_ALREADY_REGISTERED = "Customer already registered with identification: ";
    public static final String CUSTOMER_NAME_EMPTY = "Customer name cannot be empty.";
    public static final String CUSTOMER_PASSWORD_REQUIRED = "Password is required.";

    public CustomerServiceException(String message) {
        super(message);
    }

    public CustomerServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
