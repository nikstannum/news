package ru.clevertec.service;

import java.util.List;
import ru.clevertec.data.util.QueryCommentParams;
import ru.clevertec.service.dto.CommentCreateDto;
import ru.clevertec.service.dto.CommentReadDto;
import ru.clevertec.service.dto.CommentUpdateDto;

public interface CommentService {
    CommentReadDto create(CommentCreateDto comment);

    List<CommentReadDto> findAll(Integer page, Integer size);

    CommentReadDto findById(Long id);

    List<CommentReadDto> findByParams(Integer page, Integer size, QueryCommentParams queryParams);

    CommentReadDto update(CommentUpdateDto comment);

    void deleteById(Long id);
}
