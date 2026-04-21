package com.ecommerce.api.exception;

public class OtpAlreadySentException extends RuntimeException {
    private final long remainingSeconds;

    public OtpAlreadySentException(String message, long remainingSeconds) {
        super(message);
        this.remainingSeconds = remainingSeconds;
    }

    public long getRemainingSeconds() {
        return remainingSeconds;
    }
}
