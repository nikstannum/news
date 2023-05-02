package ru.clevertec.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class SuchEntityExistsException extends RuntimeException {
    public SuchEntityExistsException(String message) {
        super(message);
    }
}
