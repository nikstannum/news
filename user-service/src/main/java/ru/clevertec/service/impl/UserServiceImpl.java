package ru.clevertec.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.clevertec.client.UserDataServiceClient;
import ru.clevertec.client.dto.UserCreateDto;
import ru.clevertec.client.dto.UserReadDto;
import ru.clevertec.client.dto.UserUpdateDto;
import ru.clevertec.client.entity.User;
import ru.clevertec.service.UserService;
import ru.clevertec.service.dto.ClientUserCreateDto;
import ru.clevertec.service.dto.ClientUserReadDto;
import ru.clevertec.service.dto.ClientUserUpdateDto;
import ru.clevertec.service.mapper.UserMapper;
import ru.clevertec.util.cache.CacheDelete;
import ru.clevertec.util.cache.CacheGet;
import ru.clevertec.util.cache.CachePutPost;
import ru.clevertec.util.logger.LogInvocation;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDataServiceClient userClient;
    private final UserMapper mapper;

    @Override
    @CacheGet
    @Cacheable(value = "user")
    @LogInvocation
    public ClientUserReadDto findById(Long id) {
        UserReadDto userReadDto = userClient.getById(id);
        return mapper.toClientUserReadDto(userReadDto);
    }

    @Override
    @LogInvocation
    public List<ClientUserReadDto> findAll(Integer page, Integer size) {
        List<UserReadDto> list = userClient.getAll(page, size);
        return list.stream()
                .map(mapper::toClientUserReadDto)
                .toList();
    }

    @Override
    @LogInvocation
    public ClientUserReadDto findByEmail(String email) {
        UserReadDto userReadDto = userClient.getByEmail(email);
        return mapper.toClientUserReadDto(userReadDto);
    }

    @Override
    @CachePutPost
    @CachePut(value = "user", key = "#result.id")
    @LogInvocation
    public ClientUserReadDto create(ClientUserCreateDto dto) {
        UserCreateDto createDto = mapper.toUserCreateDto(dto);
        ResponseEntity<UserReadDto> createdResponse = userClient.create(createDto);
        UserReadDto createdUserReadDto = createdResponse.getBody();
        return mapper.toClientUserReadDto(createdUserReadDto);
    }

    @Override
    @CachePutPost
    @CachePut(value = "user", key = "#clientUserUpdateDto.id")
    @LogInvocation
    public ClientUserReadDto update(ClientUserUpdateDto clientUserUpdateDto) { // FIXME роль меняет только АДМИН
        UserUpdateDto user = mapper.toUserUpdateDto(clientUserUpdateDto);
        UserReadDto updated = userClient.update(user.getId(), user);
        return mapper.toClientUserReadDto(updated);
    }

    @Override
    @CacheDelete
    @CacheEvict(value = "user", key = "#id")
    @LogInvocation
    public void delete(Long id) {
        userClient.deleteById(id);
    }
}
