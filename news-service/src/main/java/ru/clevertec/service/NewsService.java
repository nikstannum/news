package ru.clevertec.service;

import java.util.List;
import ru.clevertec.service.dto.NewsCreateUpdateDto;
import ru.clevertec.service.dto.NewsReadDto;
import ru.clevertec.service.dto.QueryParamsNews;
import ru.clevertec.service.dto.SimpleNewsReadDto;

public interface NewsService {

    NewsReadDto findById(Long id, Integer page, Integer size);

    List<SimpleNewsReadDto> findAll(Integer page, Integer size);

    List<SimpleNewsReadDto> findByParams(Integer page, Integer size, String keyWord, QueryParamsNews params);

    NewsReadDto create(NewsCreateUpdateDto news);

    SimpleNewsReadDto update(Long id, NewsCreateUpdateDto news);

    void delete(Long id);
}
