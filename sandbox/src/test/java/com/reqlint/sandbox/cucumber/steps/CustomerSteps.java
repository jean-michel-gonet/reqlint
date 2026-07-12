package com.reqlint.sandbox.cucumber.steps;

import com.reqlint.sandbox.services.customer.properties.CustomerServiceProperties;
import io.cucumber.java.en.Given;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomerSteps {
    @Autowired
    private CustomerServiceProperties properties;

    @Given("Customer service has the following customers")
    public void customer_service_has_the_following_customers() {
        Assertions.assertThat(properties).isNotNull();
    }
}
