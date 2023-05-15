package ru.clevertec.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.service.AuthenticationService;
import ru.clevertec.service.dto.ErrorDto;
import ru.clevertec.service.dto.LoginDto;
import ru.clevertec.service.dto.ValidationResultDto;
import ru.clevertec.service.exception.ValidationException;
import ru.clevertec.service.token.JwtToken;
import ru.clevertec.service.token.RefreshJwtToken;

@Tag(name = "AuthenticationController", description = "Rest api for authentication management.")
@RestController
@RequestMapping("/v1/security")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @Operation(description = "Endpoint for obtaining a JWT token for interacting with the news service. To receive a token, the user must be " +
            "registered. Registration of a new user is carried out in the user service.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JwtToken.class))),
            @ApiResponse(responseCode = "401", description = "User wasn't authorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "409", description = "Unprocessable entity. If the login does not have an email format or password is too " +
                    "short",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationResultDto.class)))})
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public JwtToken login(@RequestBody @Valid LoginDto loginDto, Errors errors) {
        checkErrors(errors);
        return service.login(loginDto);
    }

    private void checkErrors(Errors errors) {
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }
    }

    @Operation(description = "Endpoint to get a new access token. To receive a new access token, the user must pass their refresh token. " +
            "If the user has not received a new access token, then his refresh token is invalid and he needs to re-login and get a new JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JwtToken.class))),
            @ApiResponse(responseCode = "401", description = "User passed invalid refresh token. The detailed reason is given in the message.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class)))})
    @PostMapping("/token")
    @ResponseStatus(HttpStatus.OK)
    public JwtToken getNewAccessToken(@RequestBody RefreshJwtToken request) {
        return service.getAccessToken(request.getRefreshToken());
    }


    @Operation(description = "Endpoint to get a new JWT token. To receive a new access token and refresh token, the user must be authorized in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JwtToken.class))),
            @ApiResponse(responseCode = "401", description = "User passed invalid refresh token. The detailed reason is given in the message.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "403", description = "The user has not been authorized.")})
    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public JwtToken getNewRefreshToken(@RequestBody RefreshJwtToken request) {
        return service.refresh(request.getRefreshToken());
    }

}
