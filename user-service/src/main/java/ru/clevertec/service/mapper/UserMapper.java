package ru.clevertec.service.mapper;

import org.mapstruct.Mapper;
import ru.clevertec.client.dto.UserCreateDto;
import ru.clevertec.client.dto.UserReadDto;
import ru.clevertec.client.dto.UserUpdateDto;
import ru.clevertec.client.entity.User;
import ru.clevertec.service.dto.ClientUserCreateDto;
import ru.clevertec.service.dto.ClientUserReadDto;
import ru.clevertec.service.dto.ClientUserUpdateDto;

@Mapper
public interface UserMapper {

    ClientUserReadDto toClientUserReadDto(UserReadDto userReadDto);

    UserUpdateDto toUserUpdateDto(ClientUserUpdateDto clientUserUpdateDto);

    UserCreateDto toUserCreateDto(ClientUserCreateDto clientUserCreateDto);

}
