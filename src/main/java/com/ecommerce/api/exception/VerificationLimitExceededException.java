package com.ecommerce.api.exception;

public class VerificationLimitExceededException extends RuntimeException{
    public VerificationLimitExceededException(String message)
    {
        super(message);
    }
}
