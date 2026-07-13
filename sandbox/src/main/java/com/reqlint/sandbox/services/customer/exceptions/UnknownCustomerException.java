package com.reqlint.sandbox.services.customer.exceptions;

public class UnknownCustomerException extends CustomerServiceException {
    public UnknownCustomerException(String customerId) {
        super("Unknown customer identifier '" + customerId + "'");
    }
}
