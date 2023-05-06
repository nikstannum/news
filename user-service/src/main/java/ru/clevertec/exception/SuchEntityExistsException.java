package ru.clevertec.exception;

public class SuchEntityExistsException extends AppNewsServiceException {
    public SuchEntityExistsException(String message) {
        super(message);
    }
}
