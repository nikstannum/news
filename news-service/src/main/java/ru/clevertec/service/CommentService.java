package ru.clevertec.service;

import java.util.List;
import ru.clevertec.service.dto.CommentCreateUpdateDto;
import ru.clevertec.service.dto.CommentReadDto;
import ru.clevertec.service.dto.QueryParamsComment;

public interface CommentService {
    CommentReadDto create(CommentCreateUpdateDto commentCreateDto);

    CommentReadDto update(Long id, CommentCreateUpdateDto commentCreateDto);

    void delete(long id);

    List<CommentReadDto> findAll(Integer page, Integer size);

    CommentReadDto findById(Long id);

    List<CommentReadDto> findByParams(Integer page, Integer size, QueryParamsComment queryParamsComment);
}
