package ru.clevertec.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.clevertec.api.dto.UserCreateDto;
import ru.clevertec.api.dto.UserReadDto;
import ru.clevertec.api.dto.UserSecureDto;
import ru.clevertec.api.dto.UserUpdateDto;
import ru.clevertec.exception.BadRequestException;
import ru.clevertec.exception.ValidationException;
import ru.clevertec.exception.error.ErrorDto;
import ru.clevertec.exception.error.ValidationResultDto;
import ru.clevertec.service.UserService;

@Tag(name = "UserController", description = "RRest api for user management on a non-public microservice.")
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private static final String EXC_MSG_ID_NOT_MATCH = "Incoming id in body doesn't match path";

    private final UserService service;


    @Operation(description = "Create user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserReadDto.class))),
            @ApiResponse(responseCode = "409", description = "Already registered user with this email",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable entity",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationResultDto.class)))})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserReadDto> create(@Parameter(description = "User data",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserCreateDto.class)))
                                              @RequestBody @Valid UserCreateDto user, Errors errors) {
        checkErrors(errors);
        UserReadDto created = service.create(user);
        return buildResponseCreated(created);
    }

    private void checkErrors(Errors errors) {
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }
    }

    private ResponseEntity<UserReadDto> buildResponseCreated(UserReadDto created) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(getLocation(created))
                .body(created);
    }

    private URI getLocation(UserReadDto created) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("v1/users/{id}")
                .buildAndExpand(created.getId())
                .toUri();
    }


    @Operation(description = "Get all users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserReadDto.class)))})})
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserReadDto> findAll(@Parameter(description = "Page number") @RequestParam Integer page,
                                     @Parameter(description = "Page size") @RequestParam Integer size) {
        return service.findAll(page, size);
    }


    @Operation(description = "Get users by their IDs.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserReadDto.class)))})})
    @PutMapping("/ids")
    @ResponseStatus(HttpStatus.OK)
    public List<UserReadDto> findUsersByIds(@Parameter(description = "Users IDs") @RequestBody List<Long> ids) {
        return service.findUsersByIds(ids);
    }


    @Operation(description = "Get user by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserReadDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class)))})
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserReadDto findById(@Parameter(description = "user's ID") @PathVariable Long id) {
        return service.findById(id);
    }

    @Operation(description = "Get user by email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserReadDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class)))})
    @GetMapping("/params")
    @ResponseStatus(HttpStatus.OK)
    public UserReadDto findByEmail(@Parameter(description = "User email") @RequestParam String email) {
        return service.findByEmail(email);
    }


    @Operation(description = "Update user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserReadDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request if URI path variable doesn't match the user id in the request body",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable entity",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationResultDto.class)))})
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserReadDto update(@Parameter(description = "User id") @PathVariable Long id,
                              @Parameter(description = "User data", content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = UserUpdateDto.class)))
                              @RequestBody @Valid UserUpdateDto userUpdateDto, Errors errors) {
        if (!Objects.equals(id, userUpdateDto.getId())) {
            throw new BadRequestException(EXC_MSG_ID_NOT_MATCH);
        }
        checkErrors(errors);
        return service.update(userUpdateDto);
    }

    @Operation(description = "Delete user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content")})
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Parameter(description = "User id") @PathVariable Long id) {
        service.deleteById(id);
    }


    @Operation(description = "Get user by email. Note: the response body contains the hashed password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserSecureDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class)))})
    @PostMapping("/secure")
    @ResponseStatus(HttpStatus.OK)
    public UserSecureDto findUser(@Parameter(description = "User email") @RequestParam String email) {
        return service.findSecureUser(email);
    }
}
