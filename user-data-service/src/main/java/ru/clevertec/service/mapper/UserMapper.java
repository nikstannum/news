package ru.clevertec.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.clevertec.service.dto.UserCreateDto;
import ru.clevertec.service.dto.UserReadDto;
import ru.clevertec.service.dto.UserSecureDto;
import ru.clevertec.service.dto.UserUpdateDto;
import ru.clevertec.data.User;

@Mapper
public interface UserMapper {

    UserReadDto toUserReadDto(User user);

    User toUser(UserCreateDto userCreateDto);

    User toUser(UserUpdateDto userUpdateDto);

    UserSecureDto toUserSecurityDto(User user);
}
