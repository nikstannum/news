package ru.clevertec.exception;

public class AppNewsDataServiceException extends RuntimeException {
    public AppNewsDataServiceException() {
    }

    public AppNewsDataServiceException(String message) {
        super(message);
    }
}
