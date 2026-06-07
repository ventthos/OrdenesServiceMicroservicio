package com.ordenes.ordenservice.exceptionhandler;

public class RetryScheduledException extends RuntimeException {
    public RetryScheduledException(String message) {
        super(message);
    }
}
