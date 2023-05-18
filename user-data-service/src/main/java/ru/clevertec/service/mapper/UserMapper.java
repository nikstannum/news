package ru.clevertec.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.clevertec.data.User;
import ru.clevertec.service.dto.UserCreateDto;
import ru.clevertec.service.dto.UserReadDto;
import ru.clevertec.service.dto.UserSecureDto;
import ru.clevertec.service.dto.UserUpdateDto;

@Mapper
public interface UserMapper {

    UserReadDto toUserReadDto(User user);

    @Mappings({@Mapping(target = "id", ignore = true),
            @Mapping(target = "role", ignore = true)})
    User toUser(UserCreateDto userCreateDto);

    User toUser(UserUpdateDto userUpdateDto);

    UserSecureDto toUserSecurityDto(User user);
}
