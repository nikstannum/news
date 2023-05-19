package ru.clevertec.service.exception;

public class AppUserServiceException extends RuntimeException {
    public AppUserServiceException() {
    }

    public AppUserServiceException(String message) {
        super(message);
    }
}
