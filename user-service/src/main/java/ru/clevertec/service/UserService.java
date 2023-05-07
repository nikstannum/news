package ru.clevertec.service;

import java.util.List;
import ru.clevertec.service.dto.UserCreateUpdateDto;
import ru.clevertec.service.dto.UserReadDto;
import ru.clevertec.util.logger.LogInvocation;

public interface UserService {

    UserReadDto findById(Long id);

    List<UserReadDto> findAll(Integer page, Integer size);

    UserReadDto findByEmail(String email);

    UserReadDto create(UserCreateUpdateDto dto);

    UserReadDto update(Long id, UserCreateUpdateDto user);

    void delete(Long id);
}
