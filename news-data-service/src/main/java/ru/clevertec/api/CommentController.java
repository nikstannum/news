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
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
import ru.clevertec.data.util.QueryCommentParams;
import ru.clevertec.exception.BadRequestException;
import ru.clevertec.exception.ValidationException;
import ru.clevertec.exception.error.ErrorDto;
import ru.clevertec.exception.error.ValidationResultDto;
import ru.clevertec.service.CommentService;
import ru.clevertec.service.dto.CommentCreateDto;
import ru.clevertec.service.dto.CommentReadDto;
import ru.clevertec.service.dto.CommentUpdateDto;

@Tag(name = "CommentController", description = "Rest api for comments management on a non-public microservice.")
@RestController
@RequestMapping("/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private static final String EXC_MSG_ID_NOT_MATCH = "Incoming id in body doesn't match path";

    private final CommentService service;


    @Operation(description = "Create comment.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comment created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommentReadDto.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable entity",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationResultDto.class)))})
    @PostMapping
    public ResponseEntity<CommentReadDto> create(@RequestBody @Valid CommentCreateDto comment, Errors errors) {
        checkErrors(errors);
        CommentReadDto created = createComment(comment);
        return buildResponseCreated(created);
    }

    private void checkErrors(Errors errors) {
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }
    }

    @CachePut(value = "Comment", key = "#comment.id")
    private CommentReadDto createComment(CommentCreateDto comment) {
        return service.create(comment);
    }

    private ResponseEntity<CommentReadDto> buildResponseCreated(CommentReadDto created) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(getLocation(created))
                .body(created);
    }

    private URI getLocation(CommentReadDto created) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("v1/comments/{id}")
                .buildAndExpand(created.getId())
                .toUri();
    }


    @Operation(description = "Get all comments. Comments list output is paginated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CommentReadDto.class)))})})
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentReadDto> findAll(@Parameter(description = "Page number") @RequestParam Integer page,
                                        @Parameter(description = "Page size") @RequestParam Integer size) {
        return service.findAll(page, size);
    }


    @Operation(description = "Get comment by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommentReadDto.class))),
            @ApiResponse(responseCode = "404", description = "Comment not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class)))})
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Cacheable(value = "Comment", key = "#id")
    public CommentReadDto findById(@Parameter(description = "comment ID") @PathVariable Long id) {
        return service.findById(id);
    }


    @Operation(description = "The operation of receiving comments based on the passed parameters. Comments list output is paginated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CommentReadDto.class)))})})
    @GetMapping("/params")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentReadDto> findByParams(@Parameter(description = "Page number") @RequestParam Integer page,
                                             @Parameter(description = "Page size") @RequestParam Integer size,
                                             QueryCommentParams queryParams) {
        return service.findByParams(page, size, queryParams);
    }

    @Operation(description = "Update comments.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommentReadDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request if URI path variable doesn't match the comment id in the request body"),
            @ApiResponse(responseCode = "404", description = "Comment not found"),
            @ApiResponse(responseCode = "422", description = "Unprocessable entity",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationResultDto.class)))})
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CommentReadDto update(@Parameter(description = "Comment id") @PathVariable Long id,
                                 @RequestBody @Valid CommentUpdateDto comment, Errors errors) {
        if (!Objects.equals(id, comment.getId())) {
            throw new BadRequestException(EXC_MSG_ID_NOT_MATCH);
        }
        checkErrors(errors);
        comment.setId(id);
        return service.update(comment);
    }


    @Operation(description = "Delete comment.")
    @ApiResponse(responseCode = "204", description = "No content")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }

}
