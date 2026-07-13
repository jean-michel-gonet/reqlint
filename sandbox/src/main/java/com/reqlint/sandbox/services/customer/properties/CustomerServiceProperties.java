package com.reqlint.sandbox.services.customer.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "customers")
public class CustomerServiceProperties {
    private List<CustomerAccountProperties> accounts;

    public List<CustomerAccountProperties> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<CustomerAccountProperties> accounts) {
        this.accounts = accounts;
    }
}
