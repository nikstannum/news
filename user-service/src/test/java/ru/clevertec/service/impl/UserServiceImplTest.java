package ru.clevertec.service.impl;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.clevertec.client.UserDataServiceClient;
import ru.clevertec.client.dto.UserCreateDto;
import ru.clevertec.client.dto.UserReadDto;
import ru.clevertec.client.dto.UserUpdateDto;
import ru.clevertec.client.entity.User.UserRole;
import ru.clevertec.service.dto.ClientUserCreateDto;
import ru.clevertec.service.dto.ClientUserReadDto;
import ru.clevertec.service.dto.ClientUserUpdateDto;
import ru.clevertec.service.mapper.UserMapper;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String TEST_EMAIL = "test@test.ru";
    public static final String TEST_PASSWORD = "testPassword";
    @Captor
    ArgumentCaptor<Long> captor;
    @Mock
    private UserDataServiceClient client;
    @Mock
    private UserMapper mapper;
    @InjectMocks
    private UserServiceImpl service;

    UserReadDto getStandardUserReadDto(Long id) {
        UserReadDto user = new UserReadDto();
        user.setId(id);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setEmail(TEST_EMAIL);
        user.setRole(UserRole.SUBSCRIBER);
        return user;
    }

    ClientUserReadDto getStandardClientUserReadDto(Long id) {
        ClientUserReadDto user = new ClientUserReadDto();
        user.setId(id);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setEmail(TEST_EMAIL);
        user.setRole(UserRole.SUBSCRIBER);
        return user;
    }

    @Test
    void checkFindByIdShouldReturnEquals() {
        UserReadDto userReadDto = getStandardUserReadDto(1L);
        Mockito.doReturn(userReadDto).when(client).getById(1L);
        ClientUserReadDto expected = getStandardClientUserReadDto(1L);
        Mockito.doReturn(expected).when(mapper).toClientUserReadDto(userReadDto);

        ClientUserReadDto actual = service.findById(1L);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void checkFindAllShouldHasSize2() {
        List<UserReadDto> expected = List.of(getStandardUserReadDto(1L), getStandardUserReadDto(2L));
        Mockito.doReturn(expected).when(client).getAll(1, 2);

        List<ClientUserReadDto> actual = service.findAll(1, 2);

        assertThat(actual).hasSize(2);
    }

    @Test
    void findByEmailShouldReturnEquals() {
        UserReadDto userReadDto = getStandardUserReadDto(1L);
        Mockito.doReturn(userReadDto).when(client).getByEmail(TEST_EMAIL);
        ClientUserReadDto expected = getStandardClientUserReadDto(1L);
        Mockito.doReturn(expected).when(mapper).toClientUserReadDto(userReadDto);

        ClientUserReadDto actual = service.findByEmail(TEST_EMAIL);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void checkCreateShouldReturnEquals() {
        ClientUserCreateDto clientUserCreateDto = getStandardClientUserCreateDto();
        UserCreateDto userCreateDto = getStandardUserCreateDto();
        Mockito.doReturn(userCreateDto).when(mapper).toUserCreateDto(clientUserCreateDto);
        UserReadDto userReadDto = getStandardUserReadDto(1L);
        ResponseEntity<UserReadDto> response = ResponseEntity.status(HttpStatus.CREATED).body(userReadDto);
        Mockito.doReturn(response).when(client).create(userCreateDto);
        ClientUserReadDto clientUserReadDto = getStandardClientUserReadDto(1L);
        Mockito.doReturn(clientUserReadDto).when(mapper).toClientUserReadDto(userReadDto);
        ClientUserReadDto expected = getStandardClientUserReadDto(1L);

        ClientUserReadDto actual = service.create(clientUserCreateDto);

        assertThat(actual).isEqualTo(expected);
    }

    private ClientUserCreateDto getStandardClientUserCreateDto() {
        ClientUserCreateDto user = new ClientUserCreateDto();
        user.setEmail(TEST_EMAIL);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setPassword(TEST_PASSWORD);
        return user;
    }

    private UserCreateDto getStandardUserCreateDto() {
        UserCreateDto user = new UserCreateDto();
        user.setEmail(TEST_EMAIL);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setPassword(TEST_PASSWORD);
        return user;
    }

    private ClientUserUpdateDto getStandardClientUserUpdateDto(Long id) {
        ClientUserUpdateDto user = new ClientUserUpdateDto();
        user.setId(id);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setEmail(TEST_EMAIL);
        user.setPassword(TEST_PASSWORD);
        user.setRole(UserRole.SUBSCRIBER);
        return user;
    }

    private UserUpdateDto getStandardUserUpdateDto(Long id) {
        UserUpdateDto user = new UserUpdateDto();
        user.setId(id);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setEmail(TEST_EMAIL);
        user.setPassword(TEST_PASSWORD);
        user.setRole(UserRole.SUBSCRIBER);
        return user;
    }

    @Test
    void checkUpdateShouldReturnEquals() {
        ClientUserUpdateDto clientUserUpdateDto = getStandardClientUserUpdateDto(1L);
        UserUpdateDto userUpdateDto = getStandardUserUpdateDto(1L);
        Mockito.doReturn(userUpdateDto).when(mapper).toUserUpdateDto(clientUserUpdateDto);
        UserReadDto userReadDto = getStandardUserReadDto(1L);
        Mockito.doReturn(userReadDto).when(client).update(1L, userUpdateDto);
        ClientUserReadDto clientUserReadDto = getStandardClientUserReadDto(1L);
        Mockito.doReturn(clientUserReadDto).when(mapper).toClientUserReadDto(userReadDto);
        ClientUserReadDto expected = getStandardClientUserReadDto(1L);

        ClientUserReadDto actual = service.update(clientUserUpdateDto);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void checkDeleteShouldCapture() {
        service.delete(1L);
        Mockito.verify(client).deleteById(captor.capture());
        Long captured = captor.getValue();
        assertThat(captured).isEqualTo(1L);
    }
}
