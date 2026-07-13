package com.reqlint.sandbox.cucumber.renderers;

import com.reqlint.sandbox.cucumber.beans.DataTableCustomerAccountProperties;
import com.reqlint.sandbox.services.customer.CustomerAccount;

import java.util.Arrays;
import java.util.List;

public class CustomerAccountTablifier implements Tablifier<CustomerAccount> {
    @Override
    public List<String> getHeaders() {
        return Arrays.stream(DataTableCustomerAccountProperties.Field.values()).map(Enum::name).toList();
    }

    @Override
    public List<String> getRow(CustomerAccount entity) {
        return List.of(
                ValueRenderer.renderString(entity::customerId),
                ValueRenderer.renderInteger(entity::tokenCapacity),
                ValueRenderer.renderInteger(entity::replenishmentRate));
    }
}
