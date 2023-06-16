package ru.practicum.shareit.exception;

public class EmailExists extends RuntimeException {
    public EmailExists(String message) {
        super(message);
    }
}