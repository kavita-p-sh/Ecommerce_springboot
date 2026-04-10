package com.ecommerce.api.exception;

/**
 * Exception thrown when a user is not authenticated or token is invalid.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}