package ru.clevertec.service.exception;

public class AppNewsServiceException extends RuntimeException {
    public AppNewsServiceException() {
    }

    public AppNewsServiceException(String message) {
        super(message);
    }

    public AppNewsServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppNewsServiceException(Throwable cause) {
        super(cause);
    }
}
