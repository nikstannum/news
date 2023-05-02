package ru.clevertec.api;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.service.NewsService;
import ru.clevertec.service.dto.NewsReadDto;
import ru.clevertec.service.dto.SimpleNewsReadDto;

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
    public NewsReadDto getById(@PathVariable Long id,
                               @RequestParam(required = false) Integer page,
                               @RequestParam(required = false) Integer size) {
        return newsService.findById(id, page, size);
    }
}
