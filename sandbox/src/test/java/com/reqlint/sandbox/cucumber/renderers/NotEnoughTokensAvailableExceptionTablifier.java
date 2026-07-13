package com.reqlint.sandbox.cucumber.renderers;

import com.reqlint.sandbox.cucumber.beans.DataTableNotEnoughTokensAvailableException;
import com.reqlint.sandbox.services.tokenbucket.exceptions.NotEnoughTokensAvailableException;

import java.util.Arrays;
import java.util.List;

public class NotEnoughTokensAvailableExceptionTablifier implements Tablifier<NotEnoughTokensAvailableException> {
    @Override
    public List<String> getHeaders() {
        return Arrays.stream(DataTableNotEnoughTokensAvailableException.Field.values()).map(Enum::name).toList();
    }

    @Override
    public List<String> getRow(NotEnoughTokensAvailableException entity) {
        return List.of(
                ValueRenderer.renderString(entity::getCustomerId),
                ValueRenderer.renderInteger(entity::getTokensToConsume),
                ValueRenderer.renderInteger(entity::availableTokens)
        );
    }
}
