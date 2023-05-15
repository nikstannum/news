package ru.clevertec.web.exc_handler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.clevertec.service.dto.ErrorDto;
import ru.clevertec.service.dto.ValidationResultDto;
import ru.clevertec.service.exception.AppUserAuthServiceException;
import ru.clevertec.service.exception.AuthenticationException;
import ru.clevertec.service.exception.NotFoundException;
import ru.clevertec.service.exception.ValidationException;

@Tag(name = "RestExceptionAdvice", description = "Class for handling exceptions")
@RestControllerAdvice("ru.clevertec")
public class RestExceptionAdvice {

    private static final String MSG_SERVER_ERROR = "Server error";
    private static final String MSG_CLIENT_ERROR = "Client error";
    private static final String MSG_AUTH_ERROR = "Authentication error";
    private static final String DEFAULT_MSG = "Unknown error";


    @Operation(summary = "Handling NotFoundException exception",
            description = "This method handles NotFoundException exception")
    @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorDto.class)))
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto error(NotFoundException e) {
        return new ErrorDto(MSG_CLIENT_ERROR, e.getMessage());
    }

    @Operation(summary = "Handling AuthenticationException exception",
            description = "This method handles AuthenticationException exception")
    @ApiResponse(responseCode = "401", content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorDto.class)))
    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorDto error(AuthenticationException e) {
        return new ErrorDto(MSG_AUTH_ERROR, e.getMessage());
    }


    @Operation(summary = "Handling AuthenticationException exception",
            description = "This method handles AuthenticationException exception")
    @ApiResponse(responseCode = "422", content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ValidationResultDto.class)))
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


    @Operation(summary = "Handling AppUserAuthServiceException exception",
            description = "This method handles AppUserAuthServiceException exception")
    @ApiResponse(responseCode = "500", content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorDto.class)))
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto error(AppUserAuthServiceException e) {
        return new ErrorDto(MSG_SERVER_ERROR, e.getMessage());
    }

    @Operation(summary = "Handling all other exceptions",
            description = "This method handles all other exceptions")
    @ApiResponse(responseCode = "500", content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorDto.class)))
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto error(Exception e) {
        return new ErrorDto(MSG_SERVER_ERROR, DEFAULT_MSG);
    }
}
