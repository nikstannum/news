package ru.clevertec.web;

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
import org.springframework.security.access.prepost.PreAuthorize;
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
import ru.clevertec.service.UserService;
import ru.clevertec.service.dto.ClientUserCreateDto;
import ru.clevertec.service.dto.ClientUserReadDto;
import ru.clevertec.service.dto.ClientUserUpdateDto;
import ru.clevertec.service.dto.error.ErrorDto;
import ru.clevertec.service.dto.error.ValidationResultDto;
import ru.clevertec.service.exception.BadRequestException;
import ru.clevertec.service.exception.ValidationException;


@Tag(name = "RestUserController", description = "Rest api for user management on a public microservice. For an unregistered user, one endpoint is available for " +
        "registration.")
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class RestUserController {

    private static final String EXC_MSG_ID_NOT_MATCH = "Incoming id in body doesn't match path";

    private final UserService userService;


    @Operation(description = "Create user. Default role - subscriber.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientUserReadDto.class))),
            @ApiResponse(responseCode = "409", description = "Already registered user with this email"),
            @ApiResponse(responseCode = "422", description = "Unprocessable entity",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationResultDto.class)))})

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ClientUserReadDto> create(@RequestBody @Valid ClientUserCreateDto dto,
                                                    Errors errors) {
        checkErrors(errors);
        ClientUserReadDto created = userService.create(dto);
        return buildResponseCreated(created);
    }

    private void checkErrors(Errors errors) {
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }
    }

    private ResponseEntity<ClientUserReadDto> buildResponseCreated(ClientUserReadDto created) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(getLocation(created))
                .body(created);
    }

    private URI getLocation(ClientUserReadDto created) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("v1/users/{id}")
                .buildAndExpand(created.getId())
                .toUri();
    }


    @Operation(description = "Get user by ID. A user with administrator rights can view data about any user. Other authorized users can get " +
            "information only about themselves")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientUserReadDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class)))})
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN') or  (#id == authentication.details)")
    public ClientUserReadDto getById(@Parameter(description = "user's ID") @PathVariable Long id) {
        return userService.findById(id);
    }


    @Operation(description = "Get all users. The endpoint is only accessible to the administrator")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ClientUserReadDto.class)))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")})
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<ClientUserReadDto> getAll(@Parameter(description = "Page number") @RequestParam Integer page,
                                          @Parameter(description = "Page size") @RequestParam Integer size) {
        return userService.findAll(page, size);
    }


    @Operation(description = "Get user by email. A user with administrator rights can view data about any user. Other authorized users can get " +
            "information only about themselves")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientUserReadDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class)))})

    @GetMapping("/params")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN') or (#email == authentication.principal)")
    public ClientUserReadDto getByEmail(@Parameter(description = "User email") @RequestParam String email) {
        return userService.findByEmail(email);
    }


    @Operation(description = "Update user. The admin has the authority to change the user's role. Other authorized users can change information about " +
            "themselves without restrictions, except for their role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientUserReadDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request if URI path variable doesn't match the user id in the request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "422", description = "Unprocessable entity",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationResultDto.class)))})

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN') or " +
            "(#id == authentication.details and #user.role == authentication.role)")
    public ClientUserReadDto update(@Parameter(description = "User id") @PathVariable Long id,
                                    @RequestBody @Valid ClientUserUpdateDto user,
                                    Errors errors) {
        if (!Objects.equals(id, user.getId())) {
            throw new BadRequestException(EXC_MSG_ID_NOT_MATCH);
        }
        checkErrors(errors);
        return userService.update(user);
    }


    @Operation(description = "Delete user. The endpoint is accessible without restrictions for the administrator. An authorized user can only delete " +
            "himself")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")})

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN') or (#id == authentication.details)")
    public void delete(@Parameter(description = "User id") @PathVariable Long id) {
        userService.delete(id);
    }
}
