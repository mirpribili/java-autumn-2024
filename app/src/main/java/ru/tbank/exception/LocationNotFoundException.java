package ru.tbank.exception;

public class LocationNotFoundException extends RuntimeException {
    public LocationNotFoundException(int id) {
        super("Локация с ID " + id + " не найдена.");
    }
}