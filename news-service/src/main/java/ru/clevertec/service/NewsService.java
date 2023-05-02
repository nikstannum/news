package ru.clevertec.service;

import java.util.List;
import ru.clevertec.service.dto.NewsReadDto;
import ru.clevertec.service.dto.SimpleNewsReadDto;

public interface NewsService {

    NewsReadDto findById(Long id, Integer page, Integer size);

    List<SimpleNewsReadDto> findAll(Integer page, Integer size);
}
