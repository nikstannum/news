package ru.clevertec.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class ValidationException extends RuntimeException {

    @Getter
    private final Errors errors;

    public ValidationException(Errors errors) {
        this.errors = errors;
    }
}
