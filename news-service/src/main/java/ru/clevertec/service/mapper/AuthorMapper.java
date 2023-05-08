package ru.clevertec.service.mapper;

import org.mapstruct.Mapper;
import ru.clevertec.client.dto.UserDto;
import ru.clevertec.client.entity.User;
import ru.clevertec.service.dto.AuthorReadDto;

@Mapper
public interface AuthorMapper {

    AuthorReadDto toDto(UserDto user);
}
