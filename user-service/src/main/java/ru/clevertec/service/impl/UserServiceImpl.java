package ru.clevertec.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.clevertec.client.entity.User;
import ru.clevertec.client.entity.User.UserRole;
import ru.clevertec.client.UserDataServiceClient;
import ru.clevertec.service.UserService;
import ru.clevertec.util.cache.CacheDelete;
import ru.clevertec.util.cache.CacheGet;
import ru.clevertec.util.cache.CachePutPost;
import ru.clevertec.service.dto.UserCreateUpdateDto;
import ru.clevertec.service.dto.UserReadDto;
import ru.clevertec.service.mapper.UserMapper;
import ru.clevertec.util.logger.LogInvocation;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDataServiceClient userClient;
    private final UserMapper mapper;

    @Override
    @CacheGet
    @LogInvocation
    public UserReadDto findById(Long id) {
        User user = userClient.getById(id);
        return mapper.convert(user);
    }

    @Override
    @LogInvocation
    public List<UserReadDto> findAll(Integer page, Integer size) {
        List<User> list = userClient.getAll(page, size);
        return list.stream().map(mapper::convert).toList();
    }

    @Override
    @LogInvocation
    public UserReadDto findByEmail(String email) {
        User user = userClient.getByEmail(email);
        return mapper.convert(user);
    }

    @Override
    @CachePutPost
    @LogInvocation
    public UserReadDto create(UserCreateUpdateDto dto) {
        User user = mapper.convert(dto);
        user.setRole(UserRole.SUBSCRIBER); // FIXME устанавливать роль должен уметь только АДМИН
        ResponseEntity<User> createdResponse = userClient.create(user);
        User createdUser = createdResponse.getBody();
        return mapper.convert(createdUser);
    }

    @Override
    @CachePutPost
    @LogInvocation
    public UserReadDto update(Long id, UserCreateUpdateDto userCreateUpdateDto) {
        User user = mapper.convert(userCreateUpdateDto);
        user.setId(id);
        user.setRole(UserRole.SUBSCRIBER); // FIXME если регистрирует админ - менять роль должен иметь возможность
        User updated = userClient.update(id, user);
        return mapper.convert(updated);
    }

    @Override
    @CacheDelete
    @LogInvocation
    public void delete(Long id) {
        userClient.deleteById(id);
    }
}
