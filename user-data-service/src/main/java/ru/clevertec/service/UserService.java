package ru.clevertec.service;

import java.util.List;
import ru.clevertec.api.dto.UserCreateDto;
import ru.clevertec.api.dto.UserReadDto;
import ru.clevertec.api.dto.UserSecureDto;
import ru.clevertec.api.dto.UserUpdateDto;

public interface UserService {
    UserReadDto create(UserCreateDto user);

    List<UserReadDto> findAll(Integer page, Integer size);

    List<UserReadDto> findUsersByIds(List<Long> ids);

    UserReadDto findById(Long id);

    UserReadDto findByEmail(String email);

    UserReadDto update(UserUpdateDto userUpdateDto);

    void deleteById(Long id);

    UserSecureDto findSecureUser(String email);
}
