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
import ru.clevertec.data.util.NewsQueryParams;
import ru.clevertec.exception.BadRequestException;
import ru.clevertec.exception.ValidationException;
import ru.clevertec.exception.error.ErrorDto;
import ru.clevertec.exception.error.ValidationResultDto;
import ru.clevertec.service.NewsService;
import ru.clevertec.service.dto.NewsCreateDto;
import ru.clevertec.service.dto.NewsReadDto;
import ru.clevertec.service.dto.NewsUpdateDto;
import ru.clevertec.service.dto.SimpleNewsReadDto;

@Tag(name = "NewsController", description = "Rest api for news management on a non-public microservice.")
@RestController
@RequestMapping("/v1/news")
@RequiredArgsConstructor
public class NewsController {

    private static final String EXC_MSG_ID_NOT_MATCH = "Incoming id in body doesn't match path";

    private final NewsService service;


    @Operation(description = "Create news.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "News created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NewsReadDto.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable entity",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationResultDto.class)))})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<NewsReadDto> create(@RequestBody @Valid NewsCreateDto news, Errors errors) {
        checkErrors(errors);
        NewsReadDto created = service.create(news);
        return buildResponseCreated(created);
    }

    private void checkErrors(Errors errors) {
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }
    }

    private ResponseEntity<NewsReadDto> buildResponseCreated(NewsReadDto created) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(getLocation(created))
                .body(created);
    }

    private URI getLocation(NewsReadDto created) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("v1/news/{id}")
                .buildAndExpand(created.getId())
                .toUri();
    }


    @Operation(description = "Get all news. News list output is paginated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = SimpleNewsReadDto.class)))})})
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<SimpleNewsReadDto> findAll(@Parameter(description = "Page number") @RequestParam Integer page,
                                           @Parameter(description = "Page size") @RequestParam Integer size) {
        return service.findAll(page, size);
    }


    @Operation(description = "Get news by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NewsReadDto.class))),
            @ApiResponse(responseCode = "404", description = "News not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class)))})
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public NewsReadDto findById(@Parameter(description = "news ID") @PathVariable Long id,
                                @Parameter(description = "Page number") @RequestParam(required = false) Integer page,
                                @Parameter(description = "Page size") @RequestParam(required = false) Integer size) {
        return service.findById(id, page, size);
    }


    @Operation(description = "The operation of receiving news based on the passed parameters. News list output is paginated. " +
            "The keyword parameter is optional. If this parameter is available, the search is carried out " +
            "by the specified keyword contained in the headline of the news or in the text of the news.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = SimpleNewsReadDto.class)))})})
    @GetMapping("/params")
    @ResponseStatus(HttpStatus.OK)
    public List<SimpleNewsReadDto> findByParams(@Parameter(description = "Page number") @RequestParam Integer page,
                                                @Parameter(description = "Page size") @RequestParam Integer size,
                                                @Parameter(description = "Keyword search")
                                                @RequestParam(value = "keyword", required = false) String keyWord,
                                                NewsQueryParams params) {
        return service.findByParams(page, size, keyWord, params);
    }


    @Operation(description = "Update news.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NewsReadDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request if URI path variable doesn't match the news id in the request body",
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
    public NewsReadDto update(@Parameter(description = "News id") @PathVariable Long id,
                              @RequestBody @Valid NewsUpdateDto news, Errors errors) {
        if (!Objects.equals(id, news.getId())) {
            throw new BadRequestException(EXC_MSG_ID_NOT_MATCH);
        }
        checkErrors(errors);
        return service.update(news);
    }

    @Operation(description = "Delete news.")
    @ApiResponse(responseCode = "204", description = "No content")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Parameter(description = "News id") @PathVariable Long id) {
        service.deleteById(id);
    }
}
