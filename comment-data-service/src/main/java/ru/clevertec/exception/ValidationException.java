package ru.clevertec.exception;

import lombok.Getter;
import org.springframework.validation.Errors;

public class ValidationException extends AppCommentDataServiceException{
    @Getter
    private final Errors errors;

    public ValidationException(Errors errors) {
        this.errors = errors;
    }
}
