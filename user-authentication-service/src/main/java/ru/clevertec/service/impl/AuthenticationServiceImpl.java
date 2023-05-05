package ru.clevertec.service.impl;

import io.jsonwebtoken.Claims;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.clevertec.client.UserDataServiceClient;
import ru.clevertec.entity.JwtToken;
import ru.clevertec.entity.LoginDto;
import ru.clevertec.entity.User;
import ru.clevertec.exception.AuthenticationException;
import ru.clevertec.service.AuthenticationService;
import ru.clevertec.service.JwtProvider;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    public static final String INVALID_PASSWORD = "Invalid password";
    public static final String INVALID_JWT_TOKEN = "Invalid JWT token";
    private final UserDataServiceClient client;
    private final JwtProvider provider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public JwtToken refresh(String refreshToken) {
        if (provider.validateRefreshToken(refreshToken)) {
            Claims claims = provider.getRefreshClaims(refreshToken);
            String login = claims.getSubject();
            User user = client.getByEmail(login);
            String accessToken = provider.generateAccessToken(user);
            String newRefreshToken = provider.generateRefreshToken(user);
            return new JwtToken(accessToken, newRefreshToken);
        }
        throw new AuthenticationException(INVALID_JWT_TOKEN);
    }

    @Override
    public JwtToken getAccessToken(String refreshToken) {
        if (provider.validateRefreshToken(refreshToken)) {
            Claims claims = provider.getRefreshClaims(refreshToken);
            String login = claims.getSubject();
            User user = client.getByEmail(login);
            String accessToken = provider.generateAccessToken(user);
            return new JwtToken(accessToken, null);
        }
        return new JwtToken(null, null);
    }

    @Override
    public JwtToken login(@NonNull LoginDto loginDto) {
        User user = client.getByEmail(loginDto.getEmail());
        String password = loginDto.getPassword();
        String hashedPassword = passwordEncoder.encode(password);
        if (user.getPassword().equals(hashedPassword)) {
            String accessToken = provider.generateAccessToken(user);
            String refreshToken = provider.generateRefreshToken(user);
            return new JwtToken(accessToken, refreshToken);
        } else {
            throw new AuthenticationException(INVALID_PASSWORD);
        }
    }
}
