package ru.clevertec.exception;

public class SuchEntityExistsException extends AppUserDataServiceException {
    public SuchEntityExistsException(String message) {
        super(message);
    }
}
