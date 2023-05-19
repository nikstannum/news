package ru.clevertec.service.exception;

public class AppUserAuthServiceException extends RuntimeException {
    public AppUserAuthServiceException() {
    }

    public AppUserAuthServiceException(String message) {
        super(message);
    }

    public AppUserAuthServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppUserAuthServiceException(Throwable cause) {
        super(cause);
    }
}
