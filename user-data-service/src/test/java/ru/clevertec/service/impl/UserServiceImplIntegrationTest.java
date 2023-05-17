package ru.clevertec.service.impl;

import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import ru.clevertec.BaseIntegrationTest;
import ru.clevertec.service.dto.UserCreateDto;
import ru.clevertec.service.dto.UserReadDto;
import ru.clevertec.service.dto.UserSecureDto;
import ru.clevertec.service.dto.UserUpdateDto;
import ru.clevertec.data.User.UserRole;
import ru.clevertec.exception.NotFoundException;
import ru.clevertec.exception.SecurityException;
import ru.clevertec.exception.SuchEntityExistsException;
import ru.clevertec.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;

class UserServiceImplIntegrationTest extends BaseIntegrationTest {
    public static final String NOT_EXISTS_EMAIL = "notexistsemail@gmail.com";
    public static final String EXISTS_EMAIL = "fedorov@gmail.com";
    private static final String PASSWORD = "password";
    private static final String TEST_EMAIL = "test@test.ru";
    private static final String LAST_NAME = "lastName";
    private static final String FIRST_NAME = "firstName";
    @Autowired
    private UserService service;
    @Autowired
    private EntityManager manager;

    @Test
    void createUserShouldReturnIdNotNull() {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setFirstName(FIRST_NAME);
        userCreateDto.setLastName(LAST_NAME);
        userCreateDto.setEmail(TEST_EMAIL);
        userCreateDto.setPassword(PASSWORD);

        UserReadDto actual = service.create(userCreateDto);

        assertThat(actual.getId()).isNotNull();
    }

    @Test
    void createUserShouldReturnThrowSuchEntityExistsException() {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setFirstName(FIRST_NAME);
        userCreateDto.setLastName(LAST_NAME);
        userCreateDto.setEmail(EXISTS_EMAIL);
        userCreateDto.setPassword(PASSWORD);

        Assertions.assertThrows(SuchEntityExistsException.class, () -> service.create(userCreateDto));
    }

    @Test
    void findAllShouldHasSize2() {
        int expectedSize = 2;
        List<UserReadDto> actual = service.findAll(1, 2);
        assertThat(actual).hasSize(expectedSize);
    }

    @Test
    void findUsersByIdsShouldReturnSize2() {
        int expectedSize = 2;
        List<Long> ids = List.of(1L, 2L);
        List<UserReadDto> actual = service.findUsersByIds(ids);

        assertThat(actual).hasSize(expectedSize);
    }

    @Test
    void findUserByIdShouldReturnNotNull() {
        UserReadDto actual = service.findById(1L);
        assertThat(actual).isNotNull();
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, 0, 200})
    void findUserByIdShouldThrowNotFoundExc(Long id) {
        Assertions.assertThrows(NotFoundException.class, () -> service.findById(id));
    }

    @Test
    void findByEmailShouldReturnNotNull() {
        UserReadDto actual = service.findByEmail(EXISTS_EMAIL);
        assertThat(actual).isNotNull();
    }

    @Test
    void findByEmailShouldThrowNotFoundExc() {
        Assertions.assertThrows(NotFoundException.class, () -> service.findByEmail(NOT_EXISTS_EMAIL));
    }

    @Test
    void checkUpdateShouldThrowSuchEntityExistsExc() {
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setId(1L);
        userUpdateDto.setEmail(EXISTS_EMAIL);
        Assertions.assertThrows(SuchEntityExistsException.class, () -> service.update(userUpdateDto));
    }

    @Test
    void checkUpdateShouldThrowEntityNotFoundExc() {
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setId(200L);
        Assertions.assertThrows(NotFoundException.class, () -> service.update(userUpdateDto));
    }

    @Test
    void checkUpdateSuchEmailNotExistInDbShouldReturnEquals() {
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setId(1L);
        userUpdateDto.setFirstName(FIRST_NAME);
        userUpdateDto.setLastName(LAST_NAME);
        userUpdateDto.setEmail(NOT_EXISTS_EMAIL);
        userUpdateDto.setPassword(PASSWORD);
        userUpdateDto.setRole(UserRole.ADMIN);
        UserReadDto actual = service.update(userUpdateDto);

        UserReadDto expected = new UserReadDto();
        expected.setId(1L);
        expected.setFirstName(FIRST_NAME);
        expected.setLastName(LAST_NAME);
        expected.setEmail(NOT_EXISTS_EMAIL);
        expected.setRole(UserRole.ADMIN);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void deleteById() {
        service.deleteById(5L);
        manager.flush();
    }

    @Test
    void findSecureUser() {
        UserSecureDto expected = new UserSecureDto();
        expected.setId(16L);
        expected.setFirstName("Valeriy");
        expected.setLastName("Fedorov");
        expected.setEmail(EXISTS_EMAIL);
        expected.setPassword("fedorov123");
        expected.setRole(UserRole.SUBSCRIBER);

        UserSecureDto actual = service.findSecureUser(EXISTS_EMAIL);
        actual.setPassword("fedorov123");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void checkFindUserSecureShouldThrowSecurityExc() {
        Assertions.assertThrows(SecurityException.class, () -> service.findSecureUser(NOT_EXISTS_EMAIL));
    }

}
