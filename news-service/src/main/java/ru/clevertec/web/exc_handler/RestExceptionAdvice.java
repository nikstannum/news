package ru.clevertec.web.exc_handler;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.clevertec.service.dto.error.ErrorDto;
import ru.clevertec.service.dto.error.ValidationResultDto;
import ru.clevertec.service.exception.AppNewsServiceException;
import ru.clevertec.service.exception.AuthenticationException;
import ru.clevertec.service.exception.BadRequestException;
import ru.clevertec.service.exception.NotFoundException;
import ru.clevertec.service.exception.ValidationException;

/**
 * Class for handling exceptions that occur during application operation.
 */
@RestControllerAdvice(basePackages = "ru.clevertec")
public class RestExceptionAdvice {
    private static final String MSG_SERVER_ERROR = "Server error";
    private static final String MSG_CLIENT_ERROR = "Client error";
    private static final String DEFAULT_MSG = "Unknown error";

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto error(BadRequestException e) {
        return new ErrorDto(MSG_CLIENT_ERROR, e.getMessage());
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto error(NotFoundException e) {
        return new ErrorDto(MSG_CLIENT_ERROR, e.getMessage());
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ValidationResultDto error(ValidationException e) {
        Map<String, List<String>> errors;
        if (Objects.nonNull(e.getErrors())) {
            errors = mapErrors(e.getErrors());
        } else {
            errors = e.getMap();
        }
        return new ValidationResultDto(errors);
    }

    private Map<String, List<String>> mapErrors(Errors rawErrors) {
        return rawErrors.getFieldErrors().stream().collect(Collectors.groupingBy(
                FieldError::getField, Collectors.mapping(FieldError::getDefaultMessage,
                        Collectors.toList())));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorDto error(AccessDeniedException e) {
        return new ErrorDto(MSG_CLIENT_ERROR, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorDto error(AuthenticationException e) {
        return new ErrorDto(MSG_CLIENT_ERROR, e.getMessage());
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto error(AppNewsServiceException e) {
        return new ErrorDto(MSG_SERVER_ERROR, e.getMessage());
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto error(Exception e) {
        return new ErrorDto(MSG_SERVER_ERROR, DEFAULT_MSG);
    }
}
