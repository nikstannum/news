package ru.clevertec.service.mapper;

import org.mapstruct.Mapper;
import ru.clevertec.data.entity.Comment;
import ru.clevertec.dto.CommentCreateDto;
import ru.clevertec.dto.CommentReadDto;
import ru.clevertec.dto.CommentUpdateDto;

@Mapper
public interface CommentMapper {

    Comment toComment(CommentCreateDto commentCreateDto);

    Comment toComment(CommentUpdateDto commentUpdateDto);

    CommentReadDto toCommentReadDto(Comment comment);


}
