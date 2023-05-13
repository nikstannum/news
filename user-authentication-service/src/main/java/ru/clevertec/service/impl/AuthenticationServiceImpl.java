package ru.clevertec.service.impl;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.clevertec.client.UserDataServiceClient;
import ru.clevertec.client.entity.User;
import ru.clevertec.service.AuthenticationService;
import ru.clevertec.service.JwtProvider;
import ru.clevertec.service.dto.LoginDto;
import ru.clevertec.service.dto.UserDto;
import ru.clevertec.service.exception.AuthenticationException;
import ru.clevertec.service.mapper.UserMapper;
import ru.clevertec.service.token.JwtToken;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private static final String INVALID_PASSWORD = "Invalid login or password";
    private static final String INVALID_JWT_TOKEN = "Invalid JWT token";
    private final UserDataServiceClient client;
    private final JwtProvider provider;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;

    @Override
    public JwtToken login(LoginDto loginDto) {
        UserDto userDto = client.getByEmail(loginDto.getEmail());
        User user = mapper.toUser(userDto);
        String password = loginDto.getPassword();
        String hashedPassword = passwordEncoder.encode(password);
        if (!user.getPassword().equals(hashedPassword)) {
            throw new AuthenticationException(INVALID_PASSWORD);
        }
        String accessToken = provider.generateAccessToken(user);
        String refreshToken = provider.generateRefreshToken(user);
        return new JwtToken(accessToken, refreshToken);
    }

    @Override
    public JwtToken getAccessToken(String refreshToken) {
        if (provider.validateRefreshToken(refreshToken)) {
            Claims claims = provider.getRefreshClaims(refreshToken);
            String login = claims.getSubject();
            UserDto userDto = client.getByEmail(login);
            User user = mapper.toUser(userDto);
            String accessToken = provider.generateAccessToken(user);
            return new JwtToken(accessToken, null);
        }
        return new JwtToken(null, null);
    }

    @Override
    public JwtToken refresh(String refreshToken) {
        if (provider.validateRefreshToken(refreshToken)) {
            Claims claims = provider.getRefreshClaims(refreshToken);
            String login = claims.getSubject();
            UserDto userDto = client.getByEmail(login);
            User user = mapper.toUser(userDto);
            String accessToken = provider.generateAccessToken(user);
            String newRefreshToken = provider.generateRefreshToken(user);
            return new JwtToken(accessToken, newRefreshToken);
        }
        throw new AuthenticationException(INVALID_JWT_TOKEN);
    }
}
