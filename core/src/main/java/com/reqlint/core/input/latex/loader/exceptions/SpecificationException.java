package com.reqlint.core.input.latex.loader.exceptions;

/**
 * Common ancestor for exceptions found while loading the software requirements.
 */
public class SpecificationException extends RuntimeException {

    /**
     * Class constructor.
     * @param message The exception message.
     */
    protected SpecificationException(String message) {
        super(String.format(message));
    }
}
