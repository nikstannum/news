package ru.clevertec.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.clevertec.data.entity.Comment;
import ru.clevertec.service.dto.CommentCreateDto;
import ru.clevertec.service.dto.CommentReadDto;
import ru.clevertec.service.dto.CommentUpdateDto;

@Mapper
public interface CommentMapper {

    @Mappings({@Mapping(target = "id", ignore = true),
            @Mapping(target = "createTime", ignore = true)})
    Comment toComment(CommentCreateDto commentCreateDto);

    @Mapping(target = "createTime", ignore = true)
    Comment toComment(CommentUpdateDto commentUpdateDto);

    CommentReadDto toCommentReadDto(Comment comment);


}
