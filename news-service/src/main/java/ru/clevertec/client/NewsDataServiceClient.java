package ru.clevertec.client;

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
import ru.clevertec.client.dto.CommentCreateDto;
import ru.clevertec.client.dto.CommentReadDto;
import ru.clevertec.client.dto.CommentUpdateDto;
import ru.clevertec.client.dto.NewsCreateDto;
import ru.clevertec.client.dto.NewsReadDto;
import ru.clevertec.client.dto.NewsUpdateDto;
import ru.clevertec.client.dto.SimpleNewsReadDto;
import ru.clevertec.service.dto.QueryParamsComment;
import ru.clevertec.service.dto.QueryParamsNews;

@FeignClient(name = "news-data-service", url = "http://localhost:8082")
public interface NewsDataServiceClient {
    // news
    @GetMapping("/api/news")
    List<SimpleNewsReadDto> getAll(@RequestParam("page") Integer page, @RequestParam("size") Integer size);

    @GetMapping("/api/news/{id}")
    NewsReadDto getById(@PathVariable("id") Long id, @RequestParam("page") Integer page, @RequestParam("size") Integer size);

    @GetMapping("/api/news/params")
    List<SimpleNewsReadDto> getByParams(@RequestParam("page") Integer page,
                                        @RequestParam("size") Integer size,
                                        @RequestParam(value = "keyword", required = false) String keyWord,
                                        @SpringQueryMap QueryParamsNews queryParams);

    @PostMapping("/api/news")
    ResponseEntity<NewsReadDto> create(@RequestBody NewsCreateDto news);

    @PutMapping("/api/news/{id}")
    NewsReadDto update(@PathVariable("id") Long id, @RequestBody NewsUpdateDto news);

    @DeleteMapping("/api/news/{id}")
    void deleteById(@PathVariable("id") Long id);


    // comments
    @GetMapping("/api/comments")
    List<CommentReadDto> getAllComments(@RequestParam("page") Integer page, @RequestParam("size") Integer size);

    @GetMapping("/api/comments/{id}")
    CommentReadDto getCommentById(@PathVariable("id") Long id);

    @GetMapping("/api/comments/params")
    List<CommentReadDto> getCommentByParams(@RequestParam("page") Integer page,
                                            @RequestParam("size") Integer size,
                                            @SpringQueryMap QueryParamsComment queryParams);

    @PostMapping("/api/comments")
    ResponseEntity<CommentReadDto> createComment(@RequestBody CommentCreateDto comment);

    @PutMapping("/api/comments/{id}")
    CommentReadDto updateComment(@PathVariable("id") Long id, @RequestBody CommentUpdateDto comment);

    @DeleteMapping("/api/comments/{id}")
    void deleteCommentById(@PathVariable("id") Long id);

}
