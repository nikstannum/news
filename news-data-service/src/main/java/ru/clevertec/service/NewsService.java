package ru.clevertec.service;

import java.util.List;
import ru.clevertec.data.util.NewsQueryParams;
import ru.clevertec.service.dto.NewsCreateDto;
import ru.clevertec.service.dto.NewsReadDto;
import ru.clevertec.service.dto.NewsUpdateDto;
import ru.clevertec.service.dto.SimpleNewsReadDto;

public interface NewsService  {
    NewsReadDto create(NewsCreateDto news);

    List<SimpleNewsReadDto> findAll(Integer page, Integer size);

    NewsReadDto findById(Long id, Integer page, Integer size);

    List<SimpleNewsReadDto> findByParams(Integer page, Integer size, String keyWord, NewsQueryParams params);

    NewsReadDto update(NewsUpdateDto news);

    void deleteById(Long id);
}
