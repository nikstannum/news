package ru.clevertec.client.news;

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

@FeignClient(name = "news-data-service", url = "http://localhost:8082")
public interface NewsDataServiceClient {
    @GetMapping("/api/news")
    List<News> getAll(@RequestParam("page") Integer page, @RequestParam("size") Integer size);

    @GetMapping("/api/news/{id}")
    News getById(@PathVariable("id") Long id);

    @GetMapping("/api/news/params")
    List<News> getByParams(@RequestParam("page") Integer page,
                           @RequestParam("size") Integer size,
                           @RequestParam(value = "keyword", required = false) String keyWord,
                           @SpringQueryMap QueryParamsNews queryParams);

    @PostMapping("/api/news")
    ResponseEntity<News> create(@RequestBody News news);

    @PutMapping("/api/news/{id}")
    News update(@PathVariable("id") Long id, @RequestBody News news);

    @DeleteMapping("/api/news/{id}")
    void deleteById(@PathVariable("id") Long id);
}
