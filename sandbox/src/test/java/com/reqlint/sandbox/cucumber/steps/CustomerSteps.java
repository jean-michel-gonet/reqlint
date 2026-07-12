package com.reqlint.sandbox.cucumber.steps;

import com.reqlint.sandbox.cucumber.beans.DataTableCustomerAccountProperties;
import com.reqlint.sandbox.services.customer.properties.CustomerAccountProperties;
import com.reqlint.sandbox.services.customer.properties.CustomerServiceProperties;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class CustomerSteps {
    @Autowired
    private CustomerServiceProperties properties;

    @DataTableType
    public CustomerAccountProperties customerAccountProperties(Map<String, String> entry) {
        return new DataTableCustomerAccountProperties(entry);
    }

    @Given("Customer service has the following customers")
    public void customer_service_has_the_following_customers(List<CustomerAccountProperties> customerAccountProperties) {
        properties.setAccounts(customerAccountProperties);
    }
}
