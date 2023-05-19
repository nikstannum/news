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

/**
 * Client for sending requests to a non-public news-data service
 */
@FeignClient(name = "news-data-service", configuration = FeignErrorDecoder.class)
public interface NewsDataServiceClient {
    // news
    @GetMapping("/v1/news")
    List<SimpleNewsReadDto> getAll(@RequestParam("page") Integer page, @RequestParam("size") Integer size);

    @GetMapping("/v1/news/{id}")
    NewsReadDto getById(@PathVariable("id") Long id, @RequestParam("page") Integer page, @RequestParam("size") Integer size);

    @GetMapping("/v1/news/params")
    List<SimpleNewsReadDto> getByParams(@RequestParam(value = "page", required = false) Integer page,
                                        @RequestParam(value = "size", required = false) Integer size,
                                        @RequestParam(value = "keyword", required = false) String keyWord,
                                        @SpringQueryMap QueryParamsNews queryParams);

    @PostMapping("/v1/news")
    ResponseEntity<NewsReadDto> create(@RequestBody NewsCreateDto news);

    @PutMapping("/v1/news/{id}")
    NewsReadDto update(@PathVariable("id") Long id, @RequestBody NewsUpdateDto news);

    @DeleteMapping("/v1/news/{id}")
    void deleteById(@PathVariable("id") Long id);


    // comments
    @GetMapping("/v1/comments")
    List<CommentReadDto> getAllComments(@RequestParam("page") Integer page, @RequestParam("size") Integer size);

    @GetMapping("/v1/comments/{id}")
    CommentReadDto getCommentById(@PathVariable("id") Long id);

    @GetMapping("/v1/comments/params")
    List<CommentReadDto> getCommentByParams(@RequestParam("page") Integer page,
                                            @RequestParam("size") Integer size,
                                            @SpringQueryMap QueryParamsComment queryParams);

    @PostMapping("/v1/comments")
    ResponseEntity<CommentReadDto> createComment(@RequestBody CommentCreateDto comment);

    @PutMapping("/v1/comments/{id}")
    CommentReadDto updateComment(@PathVariable("id") Long id, @RequestBody CommentUpdateDto comment);

    @DeleteMapping("/v1/comments/{id}")
    void deleteCommentById(@PathVariable("id") Long id);

}
