package ru.clevertec.api.exc_handler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.clevertec.exception.AppUserDataServiceException;
import ru.clevertec.exception.BadRequestException;
import ru.clevertec.exception.NotFoundException;
import ru.clevertec.exception.SecurityException;
import ru.clevertec.exception.SuchEntityExistsException;
import ru.clevertec.exception.ValidationException;
import ru.clevertec.exception.error.ErrorDto;
import ru.clevertec.exception.error.ValidationResultDto;

@Tag(name = "RestExceptionAdvice", description = "Class for handling exceptions")
@RestControllerAdvice("ru.clevertec")
public class RestExceptionAdvice {
    private static final String MSG_SERVER_ERROR = "Server error";
    private static final String MSG_CLIENT_ERROR = "Client error";
    private static final String DEFAULT_MSG = "Unknown error";

    @Operation(summary = "Handling BadRequestException exception",
            description = "This method handles BadRequestException exception")
    @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorDto.class)))
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto error(BadRequestException e) {
        return new ErrorDto(MSG_CLIENT_ERROR, e.getMessage());
    }


    @Operation(summary = "Handling NotFoundException exception",
            description = "This method handles NotFoundException exception")
    @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorDto.class)))
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto error(NotFoundException e) {
        return new ErrorDto(MSG_CLIENT_ERROR, e.getMessage());
    }


    @Operation(summary = "Handling SuchEntityExistsException exception",
            description = "This method handles SuchEntityExistsException exception")
    @ApiResponse(responseCode = "409", content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorDto.class)))
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDto error(SuchEntityExistsException e) {
        return new ErrorDto(MSG_CLIENT_ERROR, e.getMessage());
    }


    @Operation(summary = "Handling AuthenticationException exception",
            description = "This method handles AuthenticationException exception")
    @ApiResponse(responseCode = "422", content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ValidationResultDto.class)))
    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ValidationResultDto error(ValidationException e) {
        Map<String, List<String>> errors = mapErrors(e.getErrors());
        return new ValidationResultDto(errors);
    }

    private Map<String, List<String>> mapErrors(Errors rawErrors) {
        return rawErrors.getFieldErrors().stream().collect(Collectors.groupingBy(
                FieldError::getField, Collectors.mapping(FieldError::getDefaultMessage,
                        Collectors.toList())));
    }


    @Operation(summary = "Handling SecurityException exception",
            description = "This method handles SecurityException exception. The main purpose of the method is to correctly handle the exception in " +
                    "the authentication service")
    @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorDto.class)))
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto error(SecurityException e) {
        return new ErrorDto(MSG_CLIENT_ERROR, e.getMessage());
    }


    @Operation(summary = "Handling AppUserDataServiceException exception",
            description = "This method handles AppUserDataServiceException exception")
    @ApiResponse(responseCode = "500", content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorDto.class)))
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto error(AppUserDataServiceException e) {
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
