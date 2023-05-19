package ru.clevertec.service.exception;

public class SuchEntityExistsException extends AppUserServiceException {
    public SuchEntityExistsException(String message) {
        super(message);
    }
}
