package com.reqlint.sandbox.services.customer.implementation;

import com.reqlint.sandbox.services.customer.CustomerAccount;
import com.reqlint.sandbox.services.customer.exceptions.UnknownCustomerException;
import com.reqlint.sandbox.services.customer.properties.CustomerAccountProperties;
import com.reqlint.sandbox.services.customer.properties.CustomerServiceProperties;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class CustomerServiceImplTest {
    private static final String CUSTOMER_1_USERNAME = "USER_1";
    private static final Integer CUSTOMER_1_TOKEN_CAPACITY = 10;
    private static final Integer CUSTOMER_1_REPLENISHMENT_RATE = 10;
    private CustomerServiceImpl underTest;

    @BeforeEach
    public void setUp() {
        CustomerServiceProperties properties = new CustomerServiceProperties();
        properties.setAccounts(List.of(new CustomerAccountProperties()
                .setCustomerId(CUSTOMER_1_USERNAME)
                .setTokenCapacity(CUSTOMER_1_TOKEN_CAPACITY)
                .setReplenishmentRatePerSecond(CUSTOMER_1_REPLENISHMENT_RATE)));

        underTest = new CustomerServiceImpl(properties);
    }

    @Test
    public void can_obtain_customer_account() {
        Assertions.assertThat(underTest.obtainCustomerAccount(CUSTOMER_1_USERNAME))
                .isEqualTo(new CustomerAccount(
                        CUSTOMER_1_USERNAME,
                        CUSTOMER_1_TOKEN_CAPACITY,
                        CUSTOMER_1_REPLENISHMENT_RATE));
    }

    @Test
    public void raises_an_exception_if_customer_identifier_is_unknown() {
        Assertions.assertThatExceptionOfType(UnknownCustomerException.class)
                        .isThrownBy(() -> underTest.obtainCustomerAccount("WHO_AM_I"));
    }
}