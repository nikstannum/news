package ru.clevertec.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.clevertec.client.UserDataServiceClient;
import ru.clevertec.client.entity.User;
import ru.clevertec.client.entity.User.UserRole;
import ru.clevertec.service.util.JwtProvider;
import ru.clevertec.service.dto.LoginDto;
import ru.clevertec.service.dto.UserDto;
import ru.clevertec.service.exception.AuthenticationException;
import ru.clevertec.service.mapper.UserMapper;
import ru.clevertec.service.token.JwtToken;
import ru.clevertec.service.util.JwtValidator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    private static final long ID = 1L;
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String EMAIL = "email@email.com";
    private static final String PASSWORD = "password";
    @Mock
    private UserDataServiceClient client;
    @Mock
    private UserMapper mapper;
    @Mock
    private PasswordEncoder encoder;
    @Mock
    private JwtProvider provider;
    @Mock
    private JwtValidator validator;
    @InjectMocks
    private AuthenticationServiceImpl service;

    @Test
    void checkLoginShouldBeSuccess() {
        UserDto userDto = getStandardUserDto();
        doReturn(userDto).when(client).getByEmail(EMAIL);
        User user = getStandardUser();
        doReturn(user).when(mapper).toUser(userDto);
        doReturn(true).when(encoder).matches(any(), any());
        String accessToken = "accessToken";
        doReturn(accessToken).when(provider).generateAccessToken(user);
        String refreshToken = "refreshToken";
        doReturn(refreshToken).when(provider).generateRefreshToken(user);
        JwtToken expected = new JwtToken(accessToken, refreshToken);

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(EMAIL);
        loginDto.setPassword(PASSWORD);
        JwtToken actual = service.login(loginDto);

        assertThat(actual).isEqualTo(expected);
    }

    private User getStandardUser() {
        User user = new User();
        user.setId(ID);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
        user.setRole(UserRole.ADMIN);
        return user;
    }

    private UserDto getStandardUserDto() {
        UserDto user = new UserDto();
        user.setId(ID);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
        user.setRole(UserRole.ADMIN);
        return user;
    }

    @Test
    void checkLoginShouldThrowAuthenticationExc() {
        UserDto userDto = getStandardUserDto();
        doReturn(userDto).when(client).getByEmail(EMAIL);
        User user = getStandardUser();
        doReturn(user).when(mapper).toUser(userDto);
        doReturn(false).when(encoder).matches(any(), any());

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(EMAIL);
        loginDto.setPassword(PASSWORD);

        Assertions.assertThrows(AuthenticationException.class, () -> service.login(loginDto));
    }

    @Test
    void checkGetAccessTokenShouldReturnEquals() {
        doReturn(true).when(validator).validateRefreshToken(Mockito.any());
        Claims claims = Jwts.claims();
        claims.setSubject(EMAIL);
        doReturn(claims).when(validator).getRefreshClaims(any());
        UserDto userDto = getStandardUserDto();
        doReturn(userDto).when(client).getByEmail(EMAIL);
        User user = getStandardUser();
        doReturn(user).when(mapper).toUser(userDto);
        String accessToken = "accessToken";
        doReturn(accessToken).when(provider).generateAccessToken(any());
        JwtToken expected = new JwtToken(accessToken, null);

        JwtToken actual = service.getAccessToken("refresh");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void checkGetAccessTokenShouldAccessTokenNull() {
        doReturn(false).when(validator).validateRefreshToken(Mockito.any());
        JwtToken actual = service.getAccessToken("refresh");
        assertThat(actual.getAccessToken()).isNull();
    }

    @Test
    void checkRefreshShouldReturnEquals() {
        doReturn(true).when(validator).validateRefreshToken(Mockito.any());
        Claims claims = Jwts.claims();
        claims.setSubject(EMAIL);
        doReturn(claims).when(validator).getRefreshClaims(any());
        UserDto userDto = getStandardUserDto();
        doReturn(userDto).when(client).getByEmail(EMAIL);
        User user = getStandardUser();
        doReturn(user).when(mapper).toUser(userDto);
        String accessToken = "accessToken";
        doReturn(accessToken).when(provider).generateAccessToken(user);
        String refreshToken = "refreshToken";
        doReturn(refreshToken).when(provider).generateRefreshToken(user);
        JwtToken expected = new JwtToken(accessToken, refreshToken);

        JwtToken actual = service.refresh("refresh");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void checkRefreshShouldRThrowAuthenticationExc() {
        doReturn(false).when(validator).validateRefreshToken(Mockito.any());
        Assertions.assertThrows(AuthenticationException.class, () -> service.refresh("refresh"));
    }
}