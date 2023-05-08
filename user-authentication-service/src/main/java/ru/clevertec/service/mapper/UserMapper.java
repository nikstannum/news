package ru.clevertec.service.mapper;

import org.mapstruct.Mapper;
import ru.clevertec.client.entity.User;
import ru.clevertec.service.dto.UserDto;

@Mapper
public interface UserMapper {
    User toUser(UserDto userDto);
}
