package com.reqlint.sandbox.services.customer;

/**
 * A customer service that provides information about their accounts.
 */
public interface CustomerService {
    /**
     * Obtains the customer account associated with the specified customer identifier.
     * @param customerId The customer identifier.
     * @return The customer account.
     */
    CustomerAccount obtainCustomerAccount(String customerId);
}
