package ru.clevertec.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.clevertec.client.dto.CommentCreateDto;
import ru.clevertec.client.dto.CommentReadDto;
import ru.clevertec.client.dto.CommentUpdateDto;
import ru.clevertec.service.dto.ClientCommentCreateDto;
import ru.clevertec.service.dto.ClientCommentReadDto;
import ru.clevertec.service.dto.ClientCommentUpdateDto;
import ru.clevertec.service.dto.ClientSimpleCommentReadDto;

@Mapper
public interface CommentMapper {

    @Mapping(target = "author", ignore = true)
    ClientCommentReadDto toClientCommentReadDto(CommentReadDto comment);

    @Mapping(target = "userId", ignore = true)
    CommentCreateDto toCommentCreateDto(ClientCommentCreateDto commentCreateDto);

    @Mapping(target = "userId", ignore = true)
    CommentUpdateDto toCommentUpdateDto(ClientCommentUpdateDto clientCommentUpdateDto);

    ClientSimpleCommentReadDto toSimpleClientReadDto(CommentReadDto commentReadDto);
}
