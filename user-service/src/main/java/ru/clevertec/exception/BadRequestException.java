package ru.clevertec.exception;

public class BadRequestException extends AppNewsServiceException {
    public BadRequestException(String message) {
        super(message);
    }
}
