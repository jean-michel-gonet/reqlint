package com.reqlint.sandbox.services.tokenbucket.exceptions;

public class TokenBucketException extends RuntimeException {
    protected TokenBucketException(String message) {
        super(message);
    }
}
