package ru.clevertec.service.exception;

public class NotFoundException extends AppNewsServiceException {
    public NotFoundException(String message) {
        super(message);
    }
}
