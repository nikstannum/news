package ru.clevertec.exception;

public class NotFoundException extends AppNewsServiceException {
    public NotFoundException(String message) {
        super(message);
    }
}
