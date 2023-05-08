package ru.clevertec.service.mapper;

import org.mapstruct.Mapper;
import ru.clevertec.api.dto.UserCreateDto;
import ru.clevertec.api.dto.UserReadDto;
import ru.clevertec.api.dto.UserSecureDto;
import ru.clevertec.api.dto.UserUpdateDto;
import ru.clevertec.data.User;

@Mapper
public interface UserMapper {

    UserReadDto toUserReadDto(User user);

    User toUser(UserCreateDto userCreateDto);

    User toUser(UserUpdateDto userUpdateDto);

    UserSecureDto toUserSecurityDto(User user);
}
