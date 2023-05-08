package ru.clevertec.service.impl;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.clevertec.client.UserDataServiceClient;
import ru.clevertec.client.entity.User;
import ru.clevertec.client.entity.User.UserRole;
import ru.clevertec.service.dto.UserCreateUpdateDto;
import ru.clevertec.service.dto.UserReadDto;
import ru.clevertec.service.dto.UserReadDto.UserReadRoleDto;
import ru.clevertec.service.mapper.UserMapper;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private static final String TEST_EMAIL = "test@test.com";
    private static final String TEST_PASSWORD = "test";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";

    @Mock
    private UserMapper mapper;
    @Mock
    private UserDataServiceClient client;
    @InjectMocks
    private UserServiceImpl service;

    @BeforeEach
    void setUp() {
    }

    @Test
    void checkFindByIdShouldReturnEquals() {
        User user = getStandardUser(1L);
        Mockito.doReturn(user).when(client).getById(1L);
        UserReadDto expected = getStandardUserDto(1L);
        Mockito.doReturn(expected).when(mapper).convert(user);

        UserReadDto actual = service.findById(1L);

        assertThat(actual).isEqualTo(expected);
    }

    private UserReadDto getStandardUserDto(Long id) {
        UserReadDto dto = new UserReadDto();
        dto.setId(id);
        dto.setRole(UserReadRoleDto.SUBSCRIBER);
        dto.setEmail(TEST_EMAIL);
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        return dto;
    }

    private User getStandardUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setRole(UserRole.SUBSCRIBER);
        user.setEmail(TEST_EMAIL);
        user.setPassword(TEST_PASSWORD);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        return user;
    }

    @Test
    void checkFindAllShouldHasSize2() {
        List<User> list = List.of(new User(), new User());
        Mockito.doReturn(list).when(client).getAll(1, 2);

        List<UserReadDto> actual = service.findAll(1, 2);

        assertThat(actual).hasSize(2);
    }

    @Test
    void findByEmailShouldReturnEquals() {
        User user = getStandardUser(1L);
        Mockito.doReturn(user).when(client).getByEmail(user.getEmail());
        UserReadDto expected = getStandardUserDto(1L);
        Mockito.doReturn(expected).when(mapper).convert(user);

        UserReadDto actual = service.findByEmail(user.getEmail());

        assertThat(actual.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void checkCreateShouldReturnEquals() {
        UserCreateUpdateDto createUpdateDto = getStandardCreateUpdateDto();
        User expected = getStandardUser(1L);
        Mockito.doReturn(expected).when(mapper).convert(createUpdateDto);
        ResponseEntity<User> response = ResponseEntity.status(201).body(expected);
        Mockito.doReturn(response).when(client).create(expected);
        UserReadDto readDto = getStandardUserDto(1L);
        Mockito.doReturn(readDto).when(mapper).convert(expected);

        UserReadDto actual = service.create(createUpdateDto);

        assertThat(actual).isEqualTo(readDto);
    }

    private UserCreateUpdateDto getStandardCreateUpdateDto() {
        UserCreateUpdateDto dto = new UserCreateUpdateDto();
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        dto.setEmail(TEST_EMAIL);
        dto.setPassword(TEST_PASSWORD);
        return dto;
    }

    @Test
    void checkUpdateShouldReturnEquals() {
        User user = getStandardUser(1L);
        UserCreateUpdateDto createUpdateDto = getStandardCreateUpdateDto();
        Mockito.doReturn(user).when(mapper).convert(createUpdateDto);
        Mockito.doReturn(user).when(client).update(1L, user);
        UserReadDto expected = getStandardUserDto(1L);
        Mockito.doReturn(expected).when(mapper).convert(user);

        UserReadDto actual = service.update(1L, createUpdateDto);

        assertThat(actual).isEqualTo(expected);
    }

    @Captor
    ArgumentCaptor<Long> captor;

    @Test
    void checkDeleteShouldCapture() {
        service.delete(1L);
        Mockito.verify(client).deleteById(captor.capture());
        Long captured = captor.getValue();
        assertThat(captured).isEqualTo(1L);
    }
}