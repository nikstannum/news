package ru.clevertec.api;

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
import ru.clevertec.dto.NewsCreateDto;
import ru.clevertec.dto.NewsReadDto;
import ru.clevertec.dto.NewsUpdateDto;
import ru.clevertec.dto.SimpleNewsReadDto;
import ru.clevertec.exception.BadRequestException;
import ru.clevertec.exception.ValidationException;
import ru.clevertec.service.NewsService;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private static final String EXC_MSG_ID_NOT_MATCH = "Incoming id in body doesn't match path";

    private final NewsService service;

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
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("api/news/{id}")
                .buildAndExpand(created.getId())
                .toUri();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<SimpleNewsReadDto> findAll(@RequestParam Integer page, @RequestParam Integer size) {
        return service.findAll(page, size);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public NewsReadDto findById(@PathVariable Long id, @RequestParam Integer page, @RequestParam Integer size) {
        return service.findById(id, page, size);
    }


    @GetMapping("/params")
    @ResponseStatus(HttpStatus.OK)
    public List<SimpleNewsReadDto> findByParams(@RequestParam Integer page,
                                                @RequestParam Integer size,
                                                @RequestParam(value = "keyword", required = false) String keyWord,
                                                NewsQueryParams params) {
        return service.findByParams(page, size, keyWord, params);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public NewsReadDto update(@PathVariable Long id, @RequestBody @Valid NewsUpdateDto news, Errors errors) {
        if (!Objects.equals(id, news.getId())) {
            throw new BadRequestException(EXC_MSG_ID_NOT_MATCH);
        }
        checkErrors(errors);
        return service.update(news);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}
