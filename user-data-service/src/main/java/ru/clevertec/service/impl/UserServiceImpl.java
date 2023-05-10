package ru.clevertec.service.impl;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import ru.clevertec.api.dto.UserCreateDto;
import ru.clevertec.api.dto.UserReadDto;
import ru.clevertec.api.dto.UserSecureDto;
import ru.clevertec.api.dto.UserUpdateDto;
import ru.clevertec.data.User;
import ru.clevertec.data.User.UserRole;
import ru.clevertec.data.UserRepository;
import ru.clevertec.exception.NotFoundException;
import ru.clevertec.exception.SecurityException;
import ru.clevertec.exception.SuchEntityExistsException;
import ru.clevertec.service.UserService;
import ru.clevertec.service.mapper.UserMapper;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String EXC_MSG_NOT_FOUND_BY_ID = "wasn't found user with id = ";
    private static final String EXC_MSG_NOT_FOUND_BY_EMAIL = "wasn't found user with email ";
    private static final String ATTRIBUTE_ID = "id";
    private static final String EXC_MSG_EMAIL_EXISTS = "Already exists user with email ";
    private static final String EXC_MSG_INVALID_LOGIN = "Invalid login";

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserReadDto create(UserCreateDto userCreateDto) {
        Optional<User> existingOpt = userRepository.findUserByEmail(userCreateDto.getEmail());
        if (existingOpt.isPresent()) {
            throw new SuchEntityExistsException(EXC_MSG_EMAIL_EXISTS + userCreateDto.getEmail());
        }
        User user = userMapper.toUser(userCreateDto);
        user.setRole(UserRole.SUBSCRIBER);
        User created = userRepository.save(user);
        return userMapper.toUserReadDto(created);
    }

    @Override
    public List<UserReadDto> findAll(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Direction.ASC, ATTRIBUTE_ID);
        Page<User> userPage = userRepository.findAll(pageable);
        return userPage.map(userMapper::toUserReadDto).toList();
    }

    @Override
    public List<UserReadDto> findUsersByIds(List<Long> ids) {
        List<User> list = userRepository.findAllById(ids);
        return list.stream()
                .map(userMapper::toUserReadDto)
                .toList();
    }

    @Override
    public UserReadDto findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException(EXC_MSG_NOT_FOUND_BY_ID + id));
        return userMapper.toUserReadDto(user);
    }

    @Override
    public UserReadDto findByEmail(String email) {
        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new NotFoundException(EXC_MSG_NOT_FOUND_BY_EMAIL + email));
        return userMapper.toUserReadDto(user);
    }

    @Override
    public UserReadDto update(UserUpdateDto userUpdateDto) {
        Optional<User> existing = userRepository.findUserByEmail(userUpdateDto.getEmail());
        if (existing.isPresent() && !existing.get().getId().equals(userUpdateDto.getId())) {
            throw new SuchEntityExistsException(EXC_MSG_EMAIL_EXISTS + userUpdateDto.getEmail());
        }
        if (!userRepository.existsById(userUpdateDto.getId())) {
            throw new NotFoundException(EXC_MSG_NOT_FOUND_BY_ID + userUpdateDto.getId());
        }
        User user = userMapper.toUser(userUpdateDto);
        User updated = userRepository.save(user);
        return userMapper.toUserReadDto(updated);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserSecureDto findSecureUser(String email) {
        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new SecurityException(EXC_MSG_INVALID_LOGIN));
        return userMapper.toUserSecurityDto(user);
    }
}
