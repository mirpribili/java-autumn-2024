package ru.tbank.utils;

public class InvalidIndexException extends IndexOutOfBoundsException {
    public InvalidIndexException(String message) {
        super(message);
    }
}