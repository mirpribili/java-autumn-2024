package ru.tbank.exception;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(int id) {
        super("Категория с ID " + id + " не найдена.");
    }
}