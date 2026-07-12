package com.reqlint.sandbox.services.customer.implementation;

import com.reqlint.sandbox.services.customer.CustomerAccount;
import com.reqlint.sandbox.services.customer.CustomerService;
import com.reqlint.sandbox.services.customer.exceptions.UnknownCustomerException;
import com.reqlint.sandbox.services.customer.properties.CustomerAccountProperties;
import com.reqlint.sandbox.services.customer.properties.CustomerServiceProperties;
import org.springframework.stereotype.Service;

/**
 * A simplistic implementation of a customer service that is entirely based on
 * the configuration properties.
 */
@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerServiceProperties properties;

    public CustomerServiceImpl(CustomerServiceProperties properties) {
        this.properties = properties;
    }

    @Override
    public CustomerAccount obtainCustomerAccount(String customerId) {
        for (CustomerAccountProperties accountProperties : properties.getAccounts()) {
            if (accountProperties.getCustomerId().equals(customerId)) {
                return new CustomerAccount(
                        customerId,
                        accountProperties.getTokenCapacity(),
                        accountProperties.getReplenishmentRatePerSecond());
            }
        }
        throw new UnknownCustomerException(customerId);
    }
}
