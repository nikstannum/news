package ru.clevertec.web;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
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
import ru.clevertec.service.NewsService;
import ru.clevertec.service.dto.NewsCreateUpdateDto;
import ru.clevertec.service.dto.NewsReadDto;
import ru.clevertec.service.dto.QueryParamsNews;
import ru.clevertec.service.dto.SimpleNewsReadDto;
import ru.clevertec.service.exception.ValidationException;

@RestController
@RequestMapping("/v1/news")
@RequiredArgsConstructor
public class RestNewsController {

    private final NewsService newsService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<SimpleNewsReadDto> getAll(@RequestParam Integer page, @RequestParam Integer size) {
        return newsService.findAll(page, size);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Cacheable(value = "News", key = "#id")
    public NewsReadDto getById(@PathVariable Long id,
                               @RequestParam(required = false) Integer page,
                               @RequestParam(required = false) Integer size) {
        return newsService.findById(id, page, size);
    }

    @GetMapping("/params")
    @ResponseStatus(HttpStatus.OK)
    public List<SimpleNewsReadDto> getByParams(@RequestParam Integer page,
                                               @RequestParam Integer size,
                                               @RequestParam(value = "keyword", required = false) String keyWord,
                                               @RequestBody QueryParamsNews params) {
        return newsService.findByParams(page, size, keyWord, params);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @CachePut(value = "News", key = "#id")
    public NewsReadDto update(@PathVariable Long id, @RequestBody @Valid NewsCreateUpdateDto news, Errors errors) {
        checkErrors(errors);
        return newsService.update(id, news);
    }

    @PostMapping
    public ResponseEntity<NewsReadDto> create(@RequestBody @Valid NewsCreateUpdateDto news, Errors errors) {
        checkErrors(errors);
        NewsReadDto created = process(news);
        return buildResponseCreated(created);
    }

    @CachePut(value = "News", key = "#id")
    private NewsReadDto process(NewsCreateUpdateDto dto) {
        return newsService.create(dto);
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
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/news/{id}")
                .buildAndExpand(created.getId())
                .toUri();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(value = "News", key = "#id")
    public void deleteByID(@PathVariable Long id) {
        newsService.delete(id);
    }
}
