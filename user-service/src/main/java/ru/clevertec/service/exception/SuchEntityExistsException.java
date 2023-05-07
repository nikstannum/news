package ru.clevertec.service.exception;

public class SuchEntityExistsException extends AppNewsServiceException {
    public SuchEntityExistsException(String message) {
        super(message);
    }
}
