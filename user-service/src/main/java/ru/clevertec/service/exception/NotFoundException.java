package ru.clevertec.service.exception;

public class NotFoundException extends AppUserServiceException {
    public NotFoundException(String message) {
        super(message);
    }
}
