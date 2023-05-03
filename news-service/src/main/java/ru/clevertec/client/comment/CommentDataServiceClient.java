package ru.clevertec.client.comment;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "comment-data-service", url = "http://localhost:8083")
public interface CommentDataServiceClient {

    @GetMapping("/api/comments")
    List<Comment> getAll(@RequestParam("page") Integer page, @RequestParam("size") Integer size);

    @GetMapping("/api/comments/{id}")
    Comment getById(@PathVariable("id") Long id);

    @GetMapping("/api/comments/params")
    List<Comment> getByParams(@RequestParam("page") Integer page,
                              @RequestParam("size") Integer size,
                              @SpringQueryMap QueryParamsComment queryParams);

    @PostMapping("/api/comments")
    ResponseEntity<Comment> create(@RequestBody Comment comment);

    @PutMapping("/api/comments/{id}")
    Comment update(@PathVariable("id") Long id, @RequestBody Comment comment);

    @DeleteMapping("/api/comments/{id}")
    void deleteById(@PathVariable("id") Long id);

    @DeleteMapping
    void deleteByNewsId(@RequestParam("news_id") Long newsId);

}
