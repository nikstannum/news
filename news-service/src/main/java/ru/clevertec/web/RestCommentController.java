package ru.clevertec.web;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
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
import ru.clevertec.service.dto.ClientCommentCreateDto;
import ru.clevertec.service.dto.ClientCommentReadDto;
import ru.clevertec.service.dto.ClientCommentUpdateDto;
import ru.clevertec.service.dto.SimpleClientCommentReadDto;
import ru.clevertec.service.exception.ValidationException;
import ru.clevertec.service.CommentService;
import ru.clevertec.service.dto.QueryParamsComment;

@RestController
@RequestMapping("/v1/comments")
@RequiredArgsConstructor
public class RestCommentController {

    private final CommentService service;

    @PostMapping
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

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    ClientCommentReadDto update(@PathVariable Long id, @RequestBody @Valid ClientCommentUpdateDto commentUpdateDto, Errors errors) {
        checkErrors(errors);
        return service.update(id, commentUpdateDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<SimpleClientCommentReadDto> getAll(@RequestParam Integer page, @RequestParam Integer size) {
        return service.findAll(page, size);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ClientCommentReadDto getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/params")
    @ResponseStatus(HttpStatus.OK)
    public List<SimpleClientCommentReadDto> getByParams(@RequestParam Integer page,
                                                  @RequestParam Integer size,
                                                  @RequestBody QueryParamsComment queryParamsComment) {
        return service.findByParams(page, size, queryParamsComment);
    }


}
