package ru.clevertec.api;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import ru.clevertec.service.CommentService;
import ru.clevertec.service.dto.CommentCreateDto;
import ru.clevertec.service.dto.CommentReadDto;
import ru.clevertec.service.dto.CommentUpdateDto;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private static final String EXC_MSG_ID_NOT_MATCH = "Incoming id in body doesn't match path";

    private final CommentService service;

    @PostMapping
    public ResponseEntity<CommentReadDto> create(@RequestBody @Valid CommentCreateDto comment) {
        CommentReadDto created = createComment(comment);
        return buildResponseCreated(created);
    }

    @CachePut(value = "Comment", key = "#comment.id")
    public CommentReadDto createComment(CommentCreateDto comment) {
        return service.create(comment);
    }

    private ResponseEntity<CommentReadDto> buildResponseCreated(CommentReadDto created) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(getLocation(created))
                .body(created);
    }

    private URI getLocation(CommentReadDto created) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("api/comments/{id}")
                .buildAndExpand(created.getId())
                .toUri();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentReadDto> findAll(@RequestParam Integer page, @RequestParam Integer size) {
        return service.findAll(page, size);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Cacheable(value = "Comment", key = "#id")
    public CommentReadDto findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/params")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentReadDto> findByParams(@RequestParam Integer page,
                                             @RequestParam Integer size,
                                             QueryCommentParams queryParams) {
        return service.findByParams(page, size, queryParams);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CommentReadDto update(@PathVariable Long id, @RequestBody CommentUpdateDto comment) {
        if (!Objects.equals(id, comment.getId())) {
            throw new BadRequestException(EXC_MSG_ID_NOT_MATCH);
        }
        comment.setId(id);
        return service.update(comment);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }

}
