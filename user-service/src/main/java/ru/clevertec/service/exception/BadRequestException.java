package ru.clevertec.service.exception;

public class BadRequestException extends AppNewsServiceException {
    public BadRequestException(String message) {
        super(message);
    }
}
