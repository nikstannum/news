package ru.clevertec.exception;

public class NotFoundException extends AppUserDataServiceException {
    public NotFoundException(String message) {
        super(message);
    }
}
