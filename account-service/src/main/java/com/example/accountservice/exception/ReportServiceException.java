package com.example.accountservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ReportServiceException extends RuntimeException {

    public static final String INVALID_DATE_FORMAT = "Invalid date format. Use yyyy-MM-dd.";
    public static final String CUSTOMER_WITHOUT_ACCOUNTS = "No accounts found for the given customer.";
    public static final String REPORT_GENERATION_ERROR = "Error while generating report.";

    public ReportServiceException(String message) {
        super(message);
    }

    public ReportServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
