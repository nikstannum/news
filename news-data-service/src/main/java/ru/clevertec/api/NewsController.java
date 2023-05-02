package ru.clevertec.api;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
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
import ru.clevertec.data.News;
import ru.clevertec.data.NewsRepository;
import ru.clevertec.data.util.QueryParams;
import ru.clevertec.data.util.SpecificationBuilder;
import ru.clevertec.exception.BadRequestException;
import ru.clevertec.exception.NotFoundException;
import ru.clevertec.exception.ValidationException;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    public static final String EXC_MSG_NOT_FOUND_BY_ID = "wasn't found news with id = ";
    public static final String EXC_MSG_ID_NOT_MATCH = "Incoming id in body doesn't match path";
    public static final String ATTRIBUTE_ID = "id";
    private final NewsRepository newsRepository;
    private final SpecificationBuilder specificationBuilder;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<News> create(@RequestBody @Valid News news, Errors errors) {
        checkErrors(errors);
        News created = newsRepository.save(news);
        return buildResponseCreated(created);
    }

    private void checkErrors(Errors errors) {
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }
    }

    private ResponseEntity<News> buildResponseCreated(News created) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(getLocation(created))
                .body(created);
    }

    private URI getLocation(News created) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("api/news/{id}")
                .buildAndExpand(created.getId())
                .toUri();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<News> findAll(@RequestParam Integer page, @RequestParam Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Direction.ASC, ATTRIBUTE_ID);
        return newsRepository.findAll(pageable).toList();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public News findById(@PathVariable Long id) {
        return newsRepository.findById(id).orElseThrow(() -> new NotFoundException(EXC_MSG_NOT_FOUND_BY_ID + id));
    }

    @GetMapping("/params")
    @ResponseStatus(HttpStatus.OK)
    public List<News> findByParams(@RequestParam Integer page,
                                   @RequestParam Integer size,
                                   @RequestParam(value = "keyword", required = false) String keyWord,
                                   @RequestBody(required = false) QueryParams params) { // FIXME see NewsDataServiceClient getByParams
        Pageable pageable = PageRequest.of(page - 1, size, Direction.ASC, ATTRIBUTE_ID);
        if (keyWord != null) {
            return newsRepository.findByTitleOrTextContains(keyWord, keyWord, pageable).toList();
        }
        Specification<News> specification = specificationBuilder.getSpecificationSelectNewsByParams(params);
        return newsRepository.findAll(specification, pageable).toList();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public News update(@PathVariable Long id, @RequestBody @Valid News news, Errors errors) {
        if (!Objects.equals(id, news.getId())) {
            throw new BadRequestException(EXC_MSG_ID_NOT_MATCH);
        }
        checkErrors(errors);
        return newsRepository.save(news);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        newsRepository.deleteById(id);
    }
}
