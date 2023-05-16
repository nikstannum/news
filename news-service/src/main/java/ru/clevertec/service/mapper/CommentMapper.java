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

    @Mapping(target = "author", source = "userId", ignore = true)
    ClientCommentReadDto toClientCommentReadDto(CommentReadDto comment);

    CommentCreateDto toCommentCreateDto(ClientCommentCreateDto commentCreateDto);

    CommentUpdateDto toCommentUpdateDto(ClientCommentUpdateDto clientCommentUpdateDto);

    ClientSimpleCommentReadDto toSimpleClientReadDto(CommentReadDto commentReadDto);
}
