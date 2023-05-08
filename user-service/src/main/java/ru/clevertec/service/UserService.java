package ru.clevertec.service;

import java.util.List;
import ru.clevertec.service.dto.ClientUserCreateDto;
import ru.clevertec.service.dto.ClientUserReadDto;
import ru.clevertec.service.dto.ClientUserUpdateDto;

public interface UserService {

    ClientUserReadDto findById(Long id);

    List<ClientUserReadDto> findAll(Integer page, Integer size);

    ClientUserReadDto findByEmail(String email);

    ClientUserReadDto create(ClientUserCreateDto dto);

    ClientUserReadDto update(ClientUserUpdateDto user);

    void delete(Long id);
}
