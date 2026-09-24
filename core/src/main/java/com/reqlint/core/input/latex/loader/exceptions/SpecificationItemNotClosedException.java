package com.reqlint.core.input.latex.loader.exceptions;

import com.reqlint.core.model.specification.SpecificationItem;

/**
 * Exception raised when the {@code equipmentrequirement} has invalid arguments.
 */
public class SpecificationItemNotClosedException extends SpecificationException {
    /**
     * Class constructor.
     *
     * @param specificationItem The unclosed specification item.
     */
    public SpecificationItemNotClosedException(SpecificationItem specificationItem) {
        super("Specification item " + specificationItem + " is not closed.");
    }
}
