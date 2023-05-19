package ru.clevertec.service.exception;

public class BadRequestException extends AppUserServiceException {
    public BadRequestException(String message) {
        super(message);
    }
}
