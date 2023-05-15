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
import ru.clevertec.service.CommentService;
import ru.clevertec.service.dto.ClientCommentCreateDto;
import ru.clevertec.service.dto.ClientCommentReadDto;
import ru.clevertec.service.dto.ClientCommentUpdateDto;
import ru.clevertec.service.dto.ClientSimpleNewsReadDto;
import ru.clevertec.service.dto.QueryParamsComment;
import ru.clevertec.service.dto.SimpleClientCommentReadDto;
import ru.clevertec.service.dto.error.ErrorDto;
import ru.clevertec.service.dto.error.ValidationResultDto;
import ru.clevertec.service.exception.BadRequestException;
import ru.clevertec.service.exception.ValidationException;

@Tag(name = "RestCommentController", description = "Rest api for comments management on a public microservice. Reading comments is available " +
        "without restrictions. Other operations require registration and the presence of certain powers. User registration is carried out in the user " +
        "service. Authorizations to perform certain operations are acquired based on the user's role in the system, which is transferred " +
        "with an access token after authorization in the user-authentication service.")
@RestController
@RequestMapping("/v1/comments")
@RequiredArgsConstructor
public class RestCommentController {

    private static final String EXC_MSG_ID_NOT_MATCH = "Incoming id in body doesn't match path";

    private final CommentService service;


    @Operation(description = "Create comment. The administrator and the subscriber has the authority to create comments. " +
            "Journalists and unauthorized users do not have access to this endpoint.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comment created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientCommentReadDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable entity",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationResultDto.class)))})
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('SUBSCRIBER') and (#comment.email == authentication.principal))")
    public ResponseEntity<ClientCommentReadDto> create(@RequestBody @Valid ClientCommentCreateDto comment, Errors errors) {
        checkErrors(errors);
        ClientCommentReadDto created = service.create(comment);
        return buildResponseCreated(created);
    }

    private ResponseEntity<ClientCommentReadDto> buildResponseCreated(ClientCommentReadDto created) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(getLocation(created))
                .body(created);
    }

    private URI getLocation(ClientCommentReadDto created) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/comments/{id}")
                .buildAndExpand(created.getId())
                .toUri();
    }


    private void checkErrors(Errors errors) {
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }
    }


    @Operation(description = "Update comments. The administrator has the authority to update any comments. In turn, the subscriber " +
            "has the authority to update only his comments. Journalists and unauthorized users do not have access to this endpoint.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientCommentReadDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request if URI path variable doesn't match the comment id in the request body",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "Comment not found"),
            @ApiResponse(responseCode = "422", description = "Unprocessable entity",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationResultDto.class)))})
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('SUBSCRIBER') and (#comment.email == authentication.principal))")
    ClientCommentReadDto update(@Parameter(description = "Comment id") @PathVariable Long id,
                                @RequestBody @Valid ClientCommentUpdateDto comment,
                                Errors errors) {
        checkErrors(errors);
        if (!Objects.equals(id, comment.getId())) {
            throw new BadRequestException(EXC_MSG_ID_NOT_MATCH);
        }
        return service.update(comment);
    }


    @Operation(description = "Delete comment. The administrator has the authority to delete any comment. In turn, the subscriber can only delete " +
            "the comment added by him. Journalists and unauthorized users do not have access to this endpoint.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class)))})
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUBSCRIBER')")
    public void delete(@Parameter(description = "Comment id") @PathVariable Long id) {
        service.delete(id);
    }

    @Operation(description = "Get all comments. The comments list retrieval operation is available to all users. Comments list output is paginated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = SimpleClientCommentReadDto.class)))})})
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<SimpleClientCommentReadDto> getAll(@Parameter(description = "Page number") @RequestParam Integer page,
                                                   @Parameter(description = "Page size") @RequestParam Integer size) {
        return service.findAll(page, size);
    }


    @Operation(description = "Get comment by ID. The comment receiving operation is available for all users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientCommentReadDto.class))),
            @ApiResponse(responseCode = "404", description = "Comment not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class)))})
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ClientCommentReadDto getById(@Parameter(description = "comment ID") @PathVariable Long id) {
        return service.findById(id);
    }


    @Operation(description = "The operation of receiving comments based on the passed parameters. The comments list retrieval operation is " +
            "available to all users. Comments list output is paginated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ClientSimpleNewsReadDto.class)))})})
    @GetMapping("/params")
    @ResponseStatus(HttpStatus.OK)
    public List<SimpleClientCommentReadDto> getByParams(@Parameter(description = "Page number") @RequestParam Integer page,
                                                        @Parameter(description = "Page size") @RequestParam Integer size,
                                                        @RequestBody QueryParamsComment queryParamsComment) {
        return service.findByParams(page, size, queryParamsComment);
    }
}
