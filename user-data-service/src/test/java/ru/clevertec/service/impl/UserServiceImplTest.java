package ru.clevertec.service.impl;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import ru.clevertec.data.User;
import ru.clevertec.data.User.UserRole;
import ru.clevertec.data.UserRepository;
import ru.clevertec.exception.NotFoundException;
import ru.clevertec.exception.SecurityException;
import ru.clevertec.exception.SuchEntityExistsException;
import ru.clevertec.service.dto.UserCreateDto;
import ru.clevertec.service.dto.UserReadDto;
import ru.clevertec.service.dto.UserSecureDto;
import ru.clevertec.service.dto.UserUpdateDto;
import ru.clevertec.service.mapper.UserMapper;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private static final String EMAIL = "test@test.ru";
    private static final String PASSWORD = "password";
    private static final String LAST_NAME = "lastName";
    private static final String FIRST_NAME = "firstName";
    @Captor
    ArgumentCaptor<Long> captor;
    @Mock
    private UserRepository repository;
    @Mock
    private UserMapper mapper;
    @InjectMocks
    private UserServiceImpl service;

    @Test
    void checkCreateShouldThrowSuchEntityExistsExc() {
        Mockito.doReturn(Optional.of(new User())).when(repository).findUserByEmail(EMAIL);
        UserCreateDto userCreateDto = getStandardUserCreateDto();

        Assertions.assertThrows(SuchEntityExistsException.class, () -> service.create(userCreateDto));
    }

    private UserCreateDto getStandardUserCreateDto() {
        UserCreateDto user = new UserCreateDto();
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
        return user;
    }

    private User getStandardUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
        user.setRole(UserRole.SUBSCRIBER);
        return user;
    }

    private UserReadDto getStandardUserReadDto(Long id) {
        UserReadDto user = new UserReadDto();
        user.setId(id);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setEmail(EMAIL);
        user.setRole(UserRole.SUBSCRIBER);
        return user;
    }

    @Test
    void checkCreateShouldReturnEquals() {
        Mockito.doReturn(Optional.empty()).when(repository).findUserByEmail(EMAIL);
        UserCreateDto userCreateDto = getStandardUserCreateDto();
        User user = new User();
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
        Mockito.doReturn(user).when(mapper).toUser(userCreateDto);
        Mockito.doReturn(getStandardUser(1L)).when(repository).save(user);
        Mockito.doReturn(getStandardUserReadDto(1L)).when(mapper).toUserReadDto(getStandardUser(1L));
        UserReadDto expected = getStandardUserReadDto(1L);

        UserReadDto actual = service.create(userCreateDto);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void checkFindAllShouldHasSize2() {
        Pageable pageable = PageRequest.of(0, 2, Direction.ASC, "id");
        Page<User> userPage = new PageImpl<>(List.of(new User(), new User()));
        Mockito.doReturn(userPage).when(repository).findAll(pageable);

        List<UserReadDto> actual = service.findAll(1, 2);

        assertThat(actual).hasSize(2);
    }

    @Test
    void checkFindUsersByIdsShouldHasSize2() {
        List<Long> ids = List.of(1L, 2L);
        User user1 = getStandardUser(1L);
        User user2 = getStandardUser(2L);
        List<User> list = List.of(user1, user2);
        Mockito.doReturn(list).when(repository).findAllById(ids);

        List<UserReadDto> actual = service.findUsersByIds(ids);

        assertThat(actual).hasSize(2);
    }

    @Test
    void checkFindByIdShouldThrowNotFoundException() {
        Mockito.doReturn(Optional.empty()).when(repository).findById(1L);
        Assertions.assertThrows(NotFoundException.class, () -> service.findById(1L));
    }

    @Test
    void checkFindByIdShouldReturnEquals() {
        User user = getStandardUser(1L);
        Mockito.doReturn(Optional.of(user)).when(repository).findById(1L);
        UserReadDto userReadDto = getStandardUserReadDto(1L);
        Mockito.doReturn(userReadDto).when(mapper).toUserReadDto(user);
        UserReadDto expected = getStandardUserReadDto(1L);

        UserReadDto actual = service.findById(1L);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void checkFindByEmailShouldReturnEquals() {
        User user = getStandardUser(1L);
        Mockito.doReturn(Optional.of(user)).when(repository).findUserByEmail(EMAIL);
        UserReadDto userReadDto = getStandardUserReadDto(1L);
        Mockito.doReturn(userReadDto).when(mapper).toUserReadDto(user);
        UserReadDto expected = getStandardUserReadDto(1L);

        UserReadDto actual = service.findByEmail(EMAIL);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void checkFindByEmailShouldThrowNotFoundExc() {
        Mockito.doReturn(Optional.empty()).when(repository).findUserByEmail(EMAIL);
        Assertions.assertThrows(NotFoundException.class, () -> service.findByEmail(EMAIL));
    }

    @Test
    void checkUpdateShouldThrowSuchEntityExistsExc() {
        User user = getStandardUser(1L);
        Mockito.doReturn(Optional.of(user)).when(repository).findUserByEmail(EMAIL);
        UserUpdateDto userUpdateDto = getStandardUserUpdateDto(2L);
        Assertions.assertThrows(SuchEntityExistsException.class, () -> service.update(userUpdateDto));
    }

    private UserUpdateDto getStandardUserUpdateDto(Long id) {
        UserUpdateDto user = new UserUpdateDto();
        user.setId(id);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
        user.setRole(UserRole.SUBSCRIBER);
        return user;
    }

    @Test
    void checkUpdateShouldThrowEntityNotFoundExc() {
        Mockito.doReturn(Optional.empty()).when(repository).findUserByEmail(EMAIL);
        Mockito.doReturn(false).when(repository).existsById(1L);
        UserUpdateDto userUpdateDto = getStandardUserUpdateDto(1L);
        Assertions.assertThrows(NotFoundException.class, () -> service.update(userUpdateDto));
    }

    @Test
    void checkUpdateSuchEmailNotExistInDbShouldReturnEquals() {
        Mockito.doReturn(Optional.empty()).when(repository).findUserByEmail(EMAIL);
        Mockito.doReturn(true).when(repository).existsById(1L);
        User user = getStandardUser(1L);
        UserUpdateDto userUpdateDto = getStandardUserUpdateDto(1L);
        Mockito.doReturn(user).when(mapper).toUser(userUpdateDto);
        Mockito.doReturn(user).when(repository).save(user);
        UserReadDto userReadDto = getStandardUserReadDto(1L);
        Mockito.doReturn(userReadDto).when(mapper).toUserReadDto(user);
        UserReadDto expected = getStandardUserReadDto(1L);

        UserReadDto actual = service.update(userUpdateDto);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void checkDeleteShouldCapture() {
        service.deleteById(1L);
        Mockito.verify(repository).deleteById(captor.capture());
        Long captured = captor.getValue();
        assertThat(captured).isEqualTo(1L);
    }

    @Test
    void checkFindUserSecureShouldThrowSecurityException() {
        Mockito.doReturn(Optional.empty()).when(repository).findUserByEmail(EMAIL);
        Assertions.assertThrows(SecurityException.class, () -> service.findSecureUser(EMAIL));
    }

    @Test
    void checkFindUserSecureShouldReturnEquals() {
        UserSecureDto userSecureDto = new UserSecureDto();
        userSecureDto.setId(1L);
        userSecureDto.setFirstName(FIRST_NAME);
        userSecureDto.setLastName(LAST_NAME);
        userSecureDto.setEmail(EMAIL);
        userSecureDto.setPassword(PASSWORD);
        userSecureDto.setRole(UserRole.SUBSCRIBER);
        User user = getStandardUser(1L);
        Mockito.doReturn(Optional.of(user)).when(repository).findUserByEmail(EMAIL);
        Mockito.doReturn(userSecureDto).when(mapper).toUserSecurityDto(user);

        UserSecureDto actual = service.findSecureUser(EMAIL);

        assertThat(actual).isEqualTo(userSecureDto);
    }
}
