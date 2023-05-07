package ru.clevertec.exception;

public class AppCommentDataServiceException extends RuntimeException{
    public AppCommentDataServiceException() {
    }

    public AppCommentDataServiceException(String message) {
        super(message);
    }

    public AppCommentDataServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppCommentDataServiceException(Throwable cause) {
        super(cause);
    }
}
