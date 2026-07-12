package com.reqlint.sandbox.services.customer.exceptions;

public class CustomerServiceException extends RuntimeException {
    protected CustomerServiceException(String message) {
        super(message);
    }
}
