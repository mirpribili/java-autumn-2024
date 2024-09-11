package ru.tbank.utils;

public class EmptyListException extends RuntimeException {
    public EmptyListException(String message) {
        super(message);
    }
}