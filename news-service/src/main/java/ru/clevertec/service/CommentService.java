package ru.clevertec.service;

import java.util.List;
import ru.clevertec.service.dto.ClientCommentCreateDto;
import ru.clevertec.service.dto.ClientCommentReadDto;
import ru.clevertec.service.dto.ClientCommentUpdateDto;
import ru.clevertec.service.dto.QueryParamsComment;
import ru.clevertec.service.dto.SimpleClientCommentReadDto;

public interface CommentService {
    ClientCommentReadDto create(ClientCommentCreateDto clientCommentCreateDto);

    ClientCommentReadDto update(Long id, ClientCommentUpdateDto clientCommentUpdateDto);

    void delete(Long id);

    List<SimpleClientCommentReadDto> findAll(Integer page, Integer size);

    ClientCommentReadDto findById(Long id);

    List<SimpleClientCommentReadDto> findByParams(Integer page, Integer size, QueryParamsComment queryParamsComment);
}
