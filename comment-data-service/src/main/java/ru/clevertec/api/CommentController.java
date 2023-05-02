package ru.clevertec.api;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.clevertec.data.Comment;
import ru.clevertec.data.CommentRepository;
import ru.clevertec.data.util.QueryParams;
import ru.clevertec.data.util.SpecificationBuilder;
import ru.clevertec.exception.BadRequestException;
import ru.clevertec.exception.NotFoundException;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    public static final String EXC_MSG_NOT_FOUND_BY_ID = "wasn't found comment with id = ";
    public static final String EXC_MSG_ID_NOT_MATCH = "Incoming id in body doesn't match path";
    public static final String ATTRIBUTE_ID = "id";
    private final CommentRepository commentRepository;
    private final SpecificationBuilder specificationBuilder;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Comment> create(@RequestBody Comment comment) {
        Comment created = commentRepository.save(comment);
        return buildResponseCreated(created);
    }

    private ResponseEntity<Comment> buildResponseCreated(Comment created) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(getLocation(created))
                .body(created);
    }

    private URI getLocation(Comment created) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("api/comments/{id}")
                .buildAndExpand(created.getId())
                .toUri();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Comment> findAll(@RequestParam Integer page, @RequestParam Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Direction.ASC, ATTRIBUTE_ID);
        return commentRepository.findAll(pageable).toList();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Comment findById(@PathVariable Long id) {
        return commentRepository.findById(id).orElseThrow(() -> new NotFoundException(EXC_MSG_NOT_FOUND_BY_ID + id));
    }

    @ModelAttribute
    QueryParams queryParams() {
        return new QueryParams();
    }

    @GetMapping("/params")
    @ResponseStatus(HttpStatus.OK)
    public List<Comment> findByParams(@RequestParam Integer page,
                                      @RequestParam Integer size,
                                      @ModelAttribute QueryParams queryParams) { // FIXME see CommentDataServiceClient getByParams
        Pageable pageable = PageRequest.of(page - 1, size, Direction.ASC, ATTRIBUTE_ID);
        Specification<Comment> specification = specificationBuilder.getSpecificationSelectCommentByParams(queryParams);
        return commentRepository.findAll(specification, pageable).toList();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Comment update(@PathVariable Long id, @RequestBody Comment comment) {
        if (!Objects.equals(id, comment.getId())) {
            throw new BadRequestException(EXC_MSG_ID_NOT_MATCH);
        }
        return commentRepository.save(comment);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        commentRepository.deleteById(id);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByNewsId(@RequestParam("news_id") Long newsId) {
        commentRepository.deleteCommentByNewsId(newsId);
    }
}
