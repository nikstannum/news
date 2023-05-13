package ru.clevertec.service.mapper;

import org.mapstruct.Mapper;
import ru.clevertec.client.dto.UserDto;
import ru.clevertec.service.dto.AuthorReadDto;

@Mapper
public interface AuthorMapper {

    AuthorReadDto toAuthor(UserDto user);
}
