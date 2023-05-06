package ru.clevertec.exception;

public class AppNewsServiceException extends RuntimeException {
    public AppNewsServiceException() {
    }

    public AppNewsServiceException(String message) {
        super(message);
    }
}
