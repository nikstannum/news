package ru.clevertec.service.exception;

public class AuthenticationException extends AppUserAuthServiceException {
    public AuthenticationException(String message) {
        super(message);
    }
}
