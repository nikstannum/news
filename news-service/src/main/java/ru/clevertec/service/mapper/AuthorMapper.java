package ru.clevertec.service.mapper;

import org.mapstruct.Mapper;
import ru.clevertec.client.user.User;
import ru.clevertec.service.dto.AuthorReadDto;

@Mapper
public interface AuthorMapper {

    AuthorReadDto toDto(User user);
}
