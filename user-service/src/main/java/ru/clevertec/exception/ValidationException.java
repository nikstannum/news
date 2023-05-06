package ru.clevertec.exception;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.springframework.validation.Errors;

public class ValidationException extends AppNewsServiceException {
    @Getter
    private Errors errors;

    @Getter
    private Map<String, List<String>> map;

    public ValidationException(Map<String, List<String>> map) {
        this.map = map;
    }

    public ValidationException(Errors errors) {
        this.errors = errors;
    }
}
