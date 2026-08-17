package com.reqlint.core.input.latex.loader.exceptions;

/**
 * Exception raised when the {@code equipmentrequirement} has invalid arguments.
 */
public class SpecificationItemMissingArgumentsException extends SpecificationException {
    /**
     * Class constructor.
     *
     * @param invalidArguments The content found instead of expected arguments
     */
    public SpecificationItemMissingArgumentsException(String invalidArguments) {
        super("Missing arguments - expecting to find '{id}{title}', but found: " + invalidArguments);
    }
}
