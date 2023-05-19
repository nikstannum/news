package ru.clevertec.exception;

public class AppUserDataServiceException extends RuntimeException {
    public AppUserDataServiceException() {
    }

    public AppUserDataServiceException(String message) {
        super(message);
    }

    public AppUserDataServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppUserDataServiceException(Throwable cause) {
        super(cause);
    }
}
