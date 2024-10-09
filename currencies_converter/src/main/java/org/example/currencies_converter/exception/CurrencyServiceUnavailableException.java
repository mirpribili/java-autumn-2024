package org.example.currencies_converter.exception;

public class CurrencyServiceUnavailableException extends RuntimeException {
    public CurrencyServiceUnavailableException(String message) {
        super(message);
    }
}