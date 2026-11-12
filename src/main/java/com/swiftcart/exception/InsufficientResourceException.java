package com.swiftcart.exception;

public class InsufficientResourceException extends RuntimeException {
    public InsufficientResourceException(String message) {
        super(message);
    }
}
