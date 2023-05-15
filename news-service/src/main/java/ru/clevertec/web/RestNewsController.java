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
import org.springframework.cache.annotation.CachePut;
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
import ru.clevertec.service.NewsService;
import ru.clevertec.service.dto.ClientNewsCreateDto;
import ru.clevertec.service.dto.ClientNewsReadDto;
import ru.clevertec.service.dto.ClientNewsUpdateDto;
import ru.clevertec.service.dto.ClientSimpleNewsReadDto;
import ru.clevertec.service.dto.QueryParamsNews;
import ru.clevertec.service.dto.error.ErrorDto;
import ru.clevertec.service.dto.error.ValidationResultDto;
import ru.clevertec.service.exception.BadRequestException;
import ru.clevertec.service.exception.ValidationException;

@Tag(name = "RestNewsController", description = "Rest api for news management on a public microservice. Reading news is available without " +
        "restrictions. Other operations require registration and the presence of certain powers. User registration is carried out in the user " +
        "service. Authorizations to perform certain operations are acquired based on the user's role in the system, which is transferred " +
        "with an access token after authorization in the user-authentication service.")
@RestController
@RequestMapping("/v1/news")
@RequiredArgsConstructor
public class RestNewsController {
    private static final String EXC_MSG_ID_NOT_MATCH = "Incoming id in body doesn't match path";

    private final NewsService newsService;


    @Operation(description = "Get all news. The news list retrieval operation is available to all users. News list output is paginated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ClientSimpleNewsReadDto.class)))})})
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ClientSimpleNewsReadDto> getAll(@Parameter(description = "Page number") @RequestParam Integer page,
                                                @Parameter(description = "Page size") @RequestParam Integer size) {
        return newsService.findAll(page, size);
    }


    @Operation(description = "Get news by ID. The news receiving operation is available for all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientNewsReadDto.class))),
            @ApiResponse(responseCode = "404", description = "News not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class)))})
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ClientNewsReadDto getById(@Parameter(description = "news ID") @PathVariable Long id,
                                     @Parameter(description = "Page number") @RequestParam(required = false) Integer page,
                                     @Parameter(description = "Page size") @RequestParam(required = false) Integer size) {
        return newsService.findById(id, page, size);
    }

    @Operation(description = "The operation of receiving news based on the passed parameters. The news list retrieval operation is available to all" +
            " users. News list output is paginated. The keyword parameter is optional. If this parameter is available, the search is carried out " +
            "by the specified keyword contained in the headline of the news or in the text of the news.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ClientSimpleNewsReadDto.class)))})})
    @GetMapping("/params")
    @ResponseStatus(HttpStatus.OK)
    public List<ClientSimpleNewsReadDto> getByParams(@Parameter(description = "Page number") @RequestParam Integer page,
                                                     @Parameter(description = "Page size") @RequestParam Integer size,
                                                     @Parameter(description = "Keyword search") @RequestParam(value = "keyword", required = false) String keyWord,
                                                     @RequestBody QueryParamsNews params) {
        return newsService.findByParams(page, size, keyWord, params);
    }


    @Operation(description = "Update news. The administrator has the authority to update any news. In turn, the journalist " +
            "has the authority to update only his news. Subscribers and unauthorized users do not have access to this endpoint.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientNewsReadDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request if URI path variable doesn't match the news id in the request body",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "News not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable entity",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationResultDto.class)))})
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('JOURNALIST') and (#news.email == authentication.principal))")
    public ClientNewsReadDto update(@Parameter(description = "News id") @PathVariable Long id,
                                    @RequestBody @Valid ClientNewsUpdateDto news,
                                    Errors errors) {
        if (!Objects.equals(id, news.getId())) {
            throw new BadRequestException(EXC_MSG_ID_NOT_MATCH);
        }
        checkErrors(errors);
        return newsService.update(id, news);
    }


    @Operation(description = "Create news. The administrator and the journalist has the authority to create news. " +
            "Subscribers and unauthorized users do not have access to this endpoint.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "News created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientNewsReadDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable entity",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationResultDto.class)))})
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('JOURNALIST') and (#news.email == authentication.principal))")
    public ResponseEntity<ClientNewsReadDto> create(@RequestBody @Valid ClientNewsCreateDto news, Errors errors) {
        checkErrors(errors);
        ClientNewsReadDto created = processCreate(news);
        return buildResponseCreated(created);
    }

    @CachePut(value = "News", key = "#id")
    private ClientNewsReadDto processCreate(ClientNewsCreateDto dto) {
        return newsService.create(dto);
    }

    private void checkErrors(Errors errors) {
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }
    }

    private ResponseEntity<ClientNewsReadDto> buildResponseCreated(ClientNewsReadDto created) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(getLocation(created))
                .body(created);
    }

    private URI getLocation(ClientNewsReadDto created) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/news/{id}")
                .buildAndExpand(created.getId())
                .toUri();
    }


    @Operation(description = "Delete news. The administrator has the authority to delete any news. In turn, the journalist can only delete the news " +
            "added by him. Subscribers and unauthorized users do not have access to this endpoint.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class)))})
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'JOURNALIST')")
    public void deleteById(@Parameter(description = "News id") @PathVariable Long id) {
        newsService.delete(id);
    }
}
