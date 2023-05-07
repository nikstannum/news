package ru.clevertec.exception;

public class AppNewsDataServiceException extends RuntimeException{
    public AppNewsDataServiceException() {
    }

    public AppNewsDataServiceException(String message) {
        super(message);
    }

    public AppNewsDataServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppNewsDataServiceException(Throwable cause) {
        super(cause);
    }
}
