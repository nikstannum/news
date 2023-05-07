package ru.clevertec.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.clevertec.client.entity.User;
import ru.clevertec.service.dto.UserCreateUpdateDto;
import ru.clevertec.service.dto.UserReadDto;

@Mapper
public interface UserMapper {

    UserReadDto convert(User user);

    @Mapping(target = "id", ignore = true)
    User convert(UserCreateUpdateDto userCreateUpdateDto);
}
