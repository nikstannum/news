package ru.clevertec.service;

import java.util.List;
import ru.clevertec.service.dto.ClientNewsCreateDto;
import ru.clevertec.service.dto.ClientNewsReadDto;
import ru.clevertec.service.dto.ClientNewsUpdateDto;
import ru.clevertec.service.dto.ClientSimpleNewsReadDto;
import ru.clevertec.service.dto.QueryParamsNews;

public interface NewsService {

    ClientNewsReadDto findById(Long id, Integer page, Integer size);

    List<ClientSimpleNewsReadDto> findAll(Integer page, Integer size);

    List<ClientSimpleNewsReadDto> findByParams(Integer page, Integer size, String keyWord, QueryParamsNews params);

    ClientNewsReadDto create(ClientNewsCreateDto news);

    ClientNewsReadDto update(Long id, ClientNewsUpdateDto news);

    void delete(Long id);
}
