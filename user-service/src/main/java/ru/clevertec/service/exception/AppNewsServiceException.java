package ru.clevertec.service.exception;

public class AppNewsServiceException extends RuntimeException {
    public AppNewsServiceException() {
    }

    public AppNewsServiceException(String message) {
        super(message);
    }
}
