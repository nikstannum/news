package ru.clevertec.web;

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
import ru.clevertec.service.exception.BadRequestException;
import ru.clevertec.service.exception.ValidationException;

@RestController
@RequestMapping("/v1/news")
@RequiredArgsConstructor
public class RestNewsController {
    private static final String EXC_MSG_ID_NOT_MATCH = "Incoming id in body doesn't match path";

    private final NewsService newsService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ClientSimpleNewsReadDto> getAll(@RequestParam Integer page, @RequestParam Integer size) {
        return newsService.findAll(page, size);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ClientNewsReadDto getById(@PathVariable Long id,
                                     @RequestParam(required = false) Integer page,
                                     @RequestParam(required = false) Integer size) {
        return newsService.findById(id, page, size);
    }

    @GetMapping("/params")
    @ResponseStatus(HttpStatus.OK)
    public List<ClientSimpleNewsReadDto> getByParams(@RequestParam Integer page,
                                                     @RequestParam Integer size,
                                                     @RequestParam(value = "keyword", required = false) String keyWord,
                                                     @RequestBody QueryParamsNews params) {
        return newsService.findByParams(page, size, keyWord, params);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('JOURNALIST') and (#news.email == authentication.principal))")
    public ClientNewsReadDto update(@PathVariable Long id, @RequestBody @Valid ClientNewsUpdateDto news, Errors errors) {
        if (!Objects.equals(id, news.getId())) {
            throw new BadRequestException(EXC_MSG_ID_NOT_MATCH);
        }
        checkErrors(errors);
        return newsService.update(id, news);
    }

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

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'JOURNALIST')")
    public void deleteById(@PathVariable Long id) {
        newsService.delete(id);
    }
}
