package ru.clevertec.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.clevertec.client.comment.Comment;
import ru.clevertec.service.dto.CommentReadDto;

@Mapper
public interface CommentMapper {

    @Mapping(target = "author", source = "userId", ignore = true)
    CommentReadDto toCommentReadDto(Comment comment);
}
