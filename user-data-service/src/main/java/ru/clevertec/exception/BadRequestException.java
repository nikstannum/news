package ru.clevertec.exception;

public class BadRequestException extends AppUserDataServiceException {
    public BadRequestException(String message) {
        super(message);
    }
}
